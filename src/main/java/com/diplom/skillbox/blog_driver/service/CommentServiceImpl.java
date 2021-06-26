package com.diplom.skillbox.blog_driver.service;

import static java.time.LocalDateTime.now;
import static java.time.ZoneOffset.UTC;

import com.diplom.skillbox.blog_driver.api.request.CommentRequest;
import com.diplom.skillbox.blog_driver.api.response.CommentResponse;
import com.diplom.skillbox.blog_driver.exception.NotFoundPostByIdException;
import com.diplom.skillbox.blog_driver.exception.NotFoundUserByEmailException;
import com.diplom.skillbox.blog_driver.model.PostComment;
import com.diplom.skillbox.blog_driver.model.User;
import com.diplom.skillbox.blog_driver.repository.CommentsRepository;
import com.diplom.skillbox.blog_driver.repository.PostRepository;
import com.diplom.skillbox.blog_driver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
  final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
  private static final int MIN_COMMENT_TEXT_LENGTH = 2;
  private final CommentsRepository commentsRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;

  public CommentServiceImpl(CommentsRepository commentsRepository,
                            PostRepository postRepository,
                            UserRepository userRepository) {
    this.commentsRepository = commentsRepository;
    this.postRepository = postRepository;
    this.userRepository = userRepository;
  }

  @Override
  public CommentResponse savePostComment(CommentRequest commentRequest, Principal principal)
      throws NotFoundUserByEmailException, NotFoundPostByIdException {
    var commentResponse = new CommentResponse();
    Map<String, String> errors = getErrors(commentRequest);
    if (!errors.isEmpty()) {
      commentResponse.setErrors(errors);
    } else {
      commentsRepository.saveAndFlush(getPostComment(commentRequest, principal));
      commentResponse.setResult(true);
      commentResponse.setId(commentsRepository.findIndexOfLastAddedComment());
    }
    return commentResponse;
  }

  private Map<String, String> getErrors(CommentRequest commentRequest) {
    var errors = new HashMap<String, String>();
    if (commentRequest.getText().length() < MIN_COMMENT_TEXT_LENGTH) {
      logger.error("The text of the comment is too short.");
      errors.put("text", "Текст комментария не задан или слишком короткий");
    }
    return errors;
  }

  private PostComment getPostComment(CommentRequest commentRequest, Principal principal)
      throws NotFoundUserByEmailException, NotFoundPostByIdException {
    int parentRequestId = commentRequest.getParentId();
    boolean commentWithParentIdIsPresent = Optional
        .of(commentsRepository.findPostCommentsByParentId(parentRequestId))
        .get().stream().findAny().isPresent();
    User authorOfComment = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new NotFoundUserByEmailException(principal.getName()));
    var postComments = new PostComment();
    postComments.setPostId(Optional
        .of(postRepository.findPostById(commentRequest.getPostId()).orElseThrow().getId())
        .orElseThrow(() -> new NotFoundPostByIdException(
            String.format("ID = %s", commentRequest.getPostId()))));
    if (commentWithParentIdIsPresent) {
      postComments.setParentId(parentRequestId);
    }
    postComments.setText(commentRequest.getText());
    postComments.setTime(now(UTC));
    postComments.setAuthor(authorOfComment);
    return postComments;
  }
}
