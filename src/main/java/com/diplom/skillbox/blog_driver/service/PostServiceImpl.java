package com.diplom.skillbox.blog_driver.service;

import static com.diplom.skillbox.blog_driver.enums.ErrorReason.ERROR_EXCEPTION;
import static com.diplom.skillbox.blog_driver.enums.ErrorReason.ERROR_TEXT;
import static com.diplom.skillbox.blog_driver.enums.ErrorReason.ERROR_TITLE;
import static com.diplom.skillbox.blog_driver.enums.GlobalSettingsCode.POST_PREMODERATION;
import static com.diplom.skillbox.blog_driver.enums.ModerationStatus.ACCEPTED;
import static com.diplom.skillbox.blog_driver.enums.ModerationStatus.DECLINED;
import static com.diplom.skillbox.blog_driver.enums.ModerationStatus.NEW;
import static com.diplom.skillbox.blog_driver.enums.Permission.MODERATE;
import static com.diplom.skillbox.blog_driver.enums.Permission.USE;
import static com.diplom.skillbox.blog_driver.enums.PostStatus.valueOf;
import static com.diplom.skillbox.blog_driver.enums.SortPosts.SORT_BY_COMMENTS;
import static com.diplom.skillbox.blog_driver.enums.SortPosts.SORT_BY_LIKE;
import static com.diplom.skillbox.blog_driver.enums.SortPosts.SORT_BY_TIME_ASC;
import static com.diplom.skillbox.blog_driver.enums.SortPosts.SORT_BY_TIME_DESC;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.time.LocalDate.parse;
import static java.time.LocalDateTime.now;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import com.diplom.skillbox.blog_driver.api.request.ChangePostStatusRequest;
import com.diplom.skillbox.blog_driver.api.request.PostRequest;
import com.diplom.skillbox.blog_driver.api.response.ErrorsResponse;
import com.diplom.skillbox.blog_driver.api.response.PostListResponse;
import com.diplom.skillbox.blog_driver.api.response.PostSpecificResponse;
import com.diplom.skillbox.blog_driver.enums.ModerationStatus;
import com.diplom.skillbox.blog_driver.enums.PostSortMode;
import com.diplom.skillbox.blog_driver.enums.PostStatus;
import com.diplom.skillbox.blog_driver.exception.NotFoundPostByIdException;
import com.diplom.skillbox.blog_driver.exception.NotFoundUserByEmailException;
import com.diplom.skillbox.blog_driver.mapper.PostListResponseMapper;
import com.diplom.skillbox.blog_driver.mapper.PostSpecificMapper;
import com.diplom.skillbox.blog_driver.mapper.PostSpecificMapperImpl;
import com.diplom.skillbox.blog_driver.model.Post;
import com.diplom.skillbox.blog_driver.model.Tag;
import com.diplom.skillbox.blog_driver.model.User;
import com.diplom.skillbox.blog_driver.repository.GlobalSettingsRepository;
import com.diplom.skillbox.blog_driver.repository.PostRepository;
import com.diplom.skillbox.blog_driver.repository.TagRepository;
import com.diplom.skillbox.blog_driver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {
  final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
  private final PostRepository postRepository;
  private final TagRepository tagRepository;
  private final UserRepository userRepository;
  private final GlobalSettingsRepository settingsRepository;
  private static final int ONE_VIEW = 1;
  private static final int MIN_TITLE_LENGTH = 3;
  private static final int MIN_TEXT_LENGTH = 50;
  private static final String ID = "ID=%d";
  private static final String RESPONSE_VALUES = "mode = {}, offset = {}, limit = {}";

  public PostServiceImpl(PostRepository postRepository,
                         TagRepository tagRepository,
                         UserRepository userRepository,
                         GlobalSettingsRepository settingsRepository) {
    this.postRepository = postRepository;
    this.tagRepository = tagRepository;
    this.userRepository = userRepository;
    this.settingsRepository = settingsRepository;
  }

  /**
   * A  method  of  retrieving  posts with all related information  for the
   * homepage  and subsections "New", "Most Discussed", "Best" and "Oldest".
   * The method displays posts sorted according  to the mode parameter (see
   * below for a more detailed description of each value of this parameter).
   * The  first  time frontend requests a list of posts, starting from zero
   * (offset) ,  in  the  amount  of  limit , then - depending on the count
   * parameter, which was returned by the server.
   *
   * @param mode   output mode (sorting)
   * @param offset offset from 0 for paging
   * @param limit  number of posts to be displayed
   * @return PostListResponse
   * @see PostListResponse
   */
  @Override
  public PostListResponse getPosts(String mode, int offset, int limit) {
    logger.info("Return PostListResponse from getPosts(): "
        + RESPONSE_VALUES, mode, offset, limit);
    return PostListResponseMapper.INSTANCE.toPostListResponse(postRepository
        .findAllPosts(now(UTC), of(offset / limit, limit,
            getSort(PostSortMode.valueOf(mode.toUpperCase())))));
  }

  @Override
  public PostListResponse getPostsByQuery(String query, int offset, int limit) {
    logger.info("Return PostListResponse from getPostsByQuery(): "
        + RESPONSE_VALUES, query, offset, limit);
    return PostListResponseMapper.INSTANCE.toPostListResponse(postRepository
        .findPostsByQuery(query.trim(), now(UTC), of(offset / limit, limit)));
  }

  @Override
  public PostListResponse getPostsByDate(String date, int offset, int limit) {
    logger.info("Return PostListResponse from getPostsByDate(): "
        + RESPONSE_VALUES, date, offset, limit);
    return PostListResponseMapper.INSTANCE.toPostListResponse(postRepository
        .findPostsByDate(parse(date, ISO_LOCAL_DATE), of(offset / limit, limit)));
  }

  @Override
  public PostListResponse getPostsByTag(String tag, int offset, int limit) {
    logger.info("Return PostListResponse from getPostsByTag(): "
        + RESPONSE_VALUES, tag, offset, limit);
    return PostListResponseMapper.INSTANCE.toPostListResponse(postRepository
        .findPostsByTags(tag.trim(), now(UTC), of(offset / limit, limit)));
  }

  /**
   * The method displays data for a specific post   for display on the post
   * page, including a list of comments and tags  associated with this post.
   * Displays  a  post  in  any  case  if the post is active (the is_active
   * parameter  in  the  database  is 1),  is  accepted  by  the  moderator
   * (the  moderation_status  parameter  is  ACCEPTED)  and the time of its
   * publication (the timestamp field) is equal to the current time or less
   * than its UTC format.
   *
   * @param postId - Post ID
   * @return PostResponse @see PostResponse
   */
  @Override
  public PostSpecificResponse getPostById(int postId) {
    var authentication = getContext().getAuthentication();
    var permission = authentication.getAuthorities().toString();
    var postById = getPostByPermission(postId, permission).orElseThrow();
    if (!permission.contains(MODERATE.getPermission())
        && !postById.getUser().getEmail().equals(authentication.getName())) {
      postById.setViewCount(postById.getViewCount() + ONE_VIEW);
      postRepository.saveAndFlush(postById);
      logger.info("Post ID={} add ViewCount.", postId);
    }
    logger.info("Return PostResponse from getPostById(): ID = {}", postId);
    return PostSpecificMapper.INSTANCE.toPostSpecificResponse(postById);
  }

  @Override
  public PostListResponse getPostsForModerate(
      String status, int offset, int limit, Principal principal) {
    if (valueOf(status.toUpperCase()).equals(PostStatus.NEW)) {
      logger.info("Return PostListResponse from getPostsForModerate(): "
          + RESPONSE_VALUES, status, offset, limit);
      return PostListResponseMapper.INSTANCE.toPostListResponse(postRepository
          .findNewActivePostsForModerate(NEW, of(offset / limit, limit)));
    }
    logger.info("Return PostListResponse from getPostsForModerate(): "
        + RESPONSE_VALUES, status, offset, limit);
    return PostListResponseMapper.INSTANCE.toPostListResponse(postRepository
        .findActivePostsForModerate(principal.getName(),
            getModerationStatus(PostStatus.valueOf(status.toUpperCase())),
            of(offset / limit, limit)));
  }

  @Override
  public PostListResponse getUserPosts(String status, int offset, int limit, Principal principal) {
    if (valueOf(status.toUpperCase()).equals(PostStatus.INACTIVE)) {
      logger.info("Return PostListResponse from getMyPosts(): "
          + RESPONSE_VALUES, status, offset, limit);
      return PostListResponseMapper.INSTANCE.toPostListResponse(postRepository
          .findUserInactivePosts(principal.getName(), of(offset / limit, limit)));
    }
    logger.info("Return PostListResponse from getMyPosts(): "
        + RESPONSE_VALUES, status, offset, limit);
    return PostListResponseMapper.INSTANCE.toPostListResponse(postRepository
        .findUserActivePosts(principal.getName(),
            getModerationStatus(valueOf(status.toUpperCase())),
            of(offset / limit, limit)));
  }

  /**
   * The method sends the post data that the user entered into the post form.
   * In case the title or post text is not set and / or is too short (shorter
   * than 3 and 50 characters, respectively),  the  method should  display an
   * error and not add the post.
   *
   * @param postRequest example:
   *                    {
   *                    "timestamp":1592338706,
   *                    "active":1,
   *                    "title":"заголовок",
   *                    "tags":["java","spring"],
   *                    "text":"Post text including <b>formatting tags</b>"
   *                    }
   * @param principal   @see Principal
   * @return PostResponse
   */
  @Override
  public ErrorsResponse addPost(PostRequest postRequest, Principal principal) {
    var errors = getErrors(postRequest);
    if (errors.isEmpty()) {
      try {
        var tagList = postRequest.getTags().stream()
            .map(tagName -> tagRepository
                .findTagsByName(tagName)
                .orElse(new Tag(tagName))).collect(toList());
        User postAuthor = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new NotFoundUserByEmailException("PostAuthor was not found"));
        postRepository.saveAndFlush(PostSpecificMapper.INSTANCE.toPost(
            tagList, postRequest, principal, postAuthor, getModerationStatusBySettings()));
      } catch (NotFoundUserByEmailException e) {
        errors.put(ERROR_EXCEPTION.get(), "Post owner was not found");
        logger.error(e.getMessage(), e);
      }
    }
    return new ErrorsResponse(errors.isEmpty(), errors);
  }

  /**
   * The  method  changes the post data with  the ID to  the one that the user
   * entered  in  the post form. If the title or post text is not set and / or
   * is too  short  (less than 3 and 50 characters, respectively), the  method
   * should display an error and not change the post.
   *
   * @param postId      ID of the updated Post
   * @param postRequest example:
   *                    {
   *                    "timestamp":1592338706,
   *                    "active":1,
   *                    "title":"заголовок",
   *                    "tags":["java","spring"],
   *                    "text":"Post text including <b>formatting tags</b>"
   *                    }
   * @return PostResponse
   * @see PostSpecificMapperImpl
   */
  @Override
  public PostSpecificResponse updatePost(int postId, PostRequest postRequest)
      throws NotFoundPostByIdException {
    var errors = getErrors(postRequest);
    var permission = getContext().getAuthentication().getAuthorities().toString();
    if (errors.isEmpty() && permission.contains(USE.getPermission())) {
      var postById = getUpdatedPostById(postId, postRequest);
      postRepository.saveAndFlush(postById);
      logger.info("Post with ID = {} was updated.", postId);
      return PostSpecificMapper.INSTANCE.toPostSpecificResponse(postById);
    }
    logger.info("Post with ID = {} was not updated. Post request has errors: {}",
        postId, errors.values().toArray());
    return PostSpecificMapper.INSTANCE.toPostSpecificResponseWithErrors(errors);
  }

  @Override
  public Map<String, Boolean> changePostStatus(ChangePostStatusRequest changePostStatusRequest,
                                               Principal principal) {
    var response = new HashMap<String, Boolean>();
    response.put("result", false);
    var optionalPost = postRepository.findPostById(changePostStatusRequest.getPostId());
    try {
      User moderator = userRepository.findByEmail(principal.getName())
          .orElseThrow(() -> new NotFoundUserByEmailException("Moderator was not found"));
      if (optionalPost.isPresent() && moderator.getIsModerator()) {
        Post post = optionalPost.get();
        post.setModerationStatus(getModerationStatus(
            valueOf(changePostStatusRequest.getDecision().toUpperCase())));
        post.setModerator(moderator);
        postRepository.saveAndFlush(post);
        response.put("result", true);
      }
    } catch (NotFoundUserByEmailException e) {
      logger.error(e.getMessage(), e);
    }
    return response;
  }

  /**
   * The  method  returns  all  posts  to  the user  with  moderator rights,
   * and to other users only posts that have passed moderation.
   *
   * @param postId     Post ID
   * @param permission user rights (user or moderator)
   * @return Post
   */
  private Optional<Post> getPostByPermission(int postId, String permission) {
    try {
      if (permission.contains(USE.getPermission())) {
        return of(postRepository.findPostById(postId)
            .orElseThrow(() -> new NotFoundPostByIdException(format(ID, postId))));
      } else {
        return of(postRepository.findAcceptedPostById(postId, now(UTC))
            .orElseThrow(() -> new NotFoundPostByIdException(format(ID, postId))));
      }
    } catch (NotFoundPostByIdException e) {
      logger.error("Post with {} not found.\n {}", postId, e);
    }
    return empty();
  }

  /**
   * The method returns a post with updated data.
   *
   * @param postId      Post ID to update
   * @param postRequest request new data Post
   * @return updated Post
   * @throws NotFoundPostByIdException if the post to update is not found
   */
  private Post getUpdatedPostById(int postId, PostRequest postRequest)
      throws NotFoundPostByIdException {
    return PostSpecificMapper.INSTANCE.toUpdatePost(
        postRequest.getTags().stream()
            .map(tagName -> tagRepository.findTagsByName(tagName)
                .orElse(new Tag(tagName))).collect(toList()),
        postRepository.findPostById(postId)
            .orElseThrow(() -> new NotFoundPostByIdException(format(ID, postId))),
        postRequest, getModerationStatusBySettings());
  }

  /**
   * The method returns the way of sorting posts depending on the input parameter.
   *
   * @param mode output mode (sorting)
   * @return Sort
   */
  private Sort getSort(PostSortMode mode) {
    Sort sort;
    switch (mode) {
      case EARLY -> sort = SORT_BY_TIME_ASC.getSortMethod();
      case POPULAR -> sort = SORT_BY_COMMENTS.getSortMethod();
      case BEST -> sort = SORT_BY_LIKE.getSortMethod();
      case RECENT -> sort = SORT_BY_TIME_DESC.getSortMethod();
      default -> {
        logger.error("Method listOfPosts. Unexpected value.");
        throw new IllegalStateException("Unexpected value.");
      }
    }
    return sort;
  }

  /**
   * The method sets a new post to the ACCEPTED status if post pre-moderation
   * is disabled in the global settings.
   *
   * @return Moderation status
   */
  private ModerationStatus getModerationStatusBySettings() {
    var moderationStatus = NEW;
    if (settingsRepository.getByCode(POST_PREMODERATION.toString()).getValue().equals(valueOf(false))) {
      moderationStatus = ACCEPTED;
    }
    return moderationStatus;
  }

  /**
   * The method returns the post status depending on the data received
   * in the request.
   *
   * @param postStatus - post status set by the moderator
   * @return Moderation status
   */
  private static ModerationStatus getModerationStatus(PostStatus postStatus) {
    ModerationStatus moderationStatus;
    switch (postStatus) {
      case PENDING -> moderationStatus = NEW;
      case PUBLISHED, ACCEPTED, ACCEPT -> moderationStatus = ACCEPTED;
      case DECLINED, DECLINE -> moderationStatus = DECLINED;
      default -> throw new IllegalStateException("Unexpected value.");
    }
    return moderationStatus;
  }

  /**
   * The method checks the content of the new post, and if there is too little
   * data, displays an error.
   *
   * @param postRequest @see PostRequest
   * @return errors to the message
   */
  private Map<String, String> getErrors(PostRequest postRequest) {
    var errors = new HashMap<String, String>();
    if (postRequest.getTitle().length() < MIN_TITLE_LENGTH) {
      errors.put(ERROR_TITLE.get(), "Заголовок слишком короткий");
      logger.error("The title of the post being added is too short.");
    }
    if (postRequest.getText().length() < MIN_TEXT_LENGTH) {
      errors.put(ERROR_TEXT.get(), "Текст публикации слишком короткий");
      logger.error("The text of the added publication is too short.");
    }
    return errors;
  }
}
