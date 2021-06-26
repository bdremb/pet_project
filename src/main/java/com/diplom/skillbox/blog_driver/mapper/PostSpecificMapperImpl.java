package com.diplom.skillbox.blog_driver.mapper;

import static com.diplom.skillbox.blog_driver.enums.Permission.MODERATE;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.ofEpochSecond;
import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toList;

import com.diplom.skillbox.blog_driver.api.request.PostRequest;
import com.diplom.skillbox.blog_driver.api.response.PostSpecificResponse;
import com.diplom.skillbox.blog_driver.api.response.UserResponse;
import com.diplom.skillbox.blog_driver.enums.ModerationStatus;
import com.diplom.skillbox.blog_driver.model.Post;
import com.diplom.skillbox.blog_driver.model.Tag;
import com.diplom.skillbox.blog_driver.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import java.security.Principal;
import java.util.List;
import java.util.Map;

public class PostSpecificMapperImpl implements PostSpecificMapper {
  private static final int NANO_OF_SECOND = 0;
  private static final int ZERO_VIEWS = 0;
  private static final int ACTIVE_POST = 1;
  private static final int LIKE = 1;
  private static final String TEXT_WITH_TAGS = "<p>%s</p>";

  @Override
  public PostSpecificResponse toPostSpecificResponse(Post post) {
    var postResponse = new PostSpecificResponse();
    int likeCount = (int) post.getPostVotes()
        .stream().filter(postVotes -> postVotes.getValue() == LIKE).count();
    postResponse.setId(post.getId());
    postResponse.setTimestamp(post.getTime().toEpochSecond(UTC));
    postResponse.setActive(post.getIsActive() == ACTIVE_POST);
    postResponse.setUser(new UserResponse(post.getUser().getId(),
        post.getUser().getName(), post.getUser().getPhoto()));
    postResponse.setTitle(post.getTitle());
    postResponse.setText(format(TEXT_WITH_TAGS, post.getText()));
    postResponse.setLikeCount(likeCount);
    postResponse.setDislikeCount(post.getPostVotes().size() - likeCount);
    postResponse.setViewCount(post.getViewCount());
    postResponse.setComments(post.getPostComments()
        .stream().map(CommentListMapper.INSTANCE::toCommentListResponse).collect(toList()));
    postResponse.setTags(post.getTags().stream().map(Tag::getName).collect(toList()));
    return postResponse;
  }

  @Override
  public Post toPost(List<Tag> tagsWithPost, PostRequest postRequest,
                     Principal principal, User authorOfPost, ModerationStatus moderationStatus) {
    var post = new Post();
    var postAddingTime = ofEpochSecond(postRequest.getTimestamp(),
        NANO_OF_SECOND, UTC);
    var currentTime = now(UTC);
    post.setTime(postAddingTime.isBefore(currentTime) ? postAddingTime : currentTime);
    post.setIsActive(postRequest.getActive());
    post.setTags(tagsWithPost);
    post.setText(postRequest.getText());
    post.setUser(authorOfPost);
    post.setViewCount(ZERO_VIEWS);
    post.setTitle(postRequest.getTitle());
    post.setModerationStatus(moderationStatus);
    return post;
  }

  /**
   * The  method  changes the post data with  the ID to  the one that the user
   * entered  in  the post form.
   * The post publication time should also be checked: if the post publication
   * time is earlier than the current time, it should automatically become the
   * current  one.  If later  than the current one, you must set the specified
   * value.
   * The post should be saved with the NEW moderation status if the author has
   * changed it,  and the moderation status should not change if the moderator
   * changed it.
   *
   * @param tags        Available tags of the post being updated
   * @param post        Post to update
   * @param postRequest example:
   *                    {
   *                    "timestamp":1592338706,
   *                    "active":1,
   *                    "title":"заголовок",
   *                    "tags":["java","spring"],
   *                    "text":"Post text including <b>formatting tags</b>"
   *                    }
   * @return updated Post
   */
  @Override
  public Post toUpdatePost(List<Tag> tags, Post post, PostRequest postRequest,
                           ModerationStatus moderationStatus) {
    var postTimestamp = postRequest.getTimestamp();
    var currentPostTime = ofEpochSecond(postTimestamp, NANO_OF_SECOND, UTC);
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    post.setIsActive(postRequest.getActive());
    post.setTime(postTimestamp < now().toEpochSecond(UTC) ? now(UTC) : currentPostTime);
    post.setTitle(postRequest.getTitle());
    post.setText(postRequest.getText());
    post.setTags(tags);
    post.setIsActive(postRequest.getActive());
    if (!authentication.getAuthorities().toString().contains(MODERATE.getPermission())
        && post.getUser().getEmail().equals(authentication.getName())) {
      post.setModerationStatus(moderationStatus);
    }
    return post;
  }

  @Override
  public PostSpecificResponse toPostSpecificResponseWithErrors(Map<String, String> errors) {
    var postResponse = new PostSpecificResponse();
    postResponse.setErrors(errors);
    return postResponse;
  }
}
