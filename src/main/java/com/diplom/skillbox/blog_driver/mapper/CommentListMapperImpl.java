package com.diplom.skillbox.blog_driver.mapper;

import static java.lang.String.format;
import static java.time.ZoneOffset.UTC;

import com.diplom.skillbox.blog_driver.api.response.CommentListResponse;
import com.diplom.skillbox.blog_driver.api.response.CommentListResponse.AuthorOfComment;
import com.diplom.skillbox.blog_driver.model.PostComment;

public class CommentListMapperImpl implements CommentListMapper {
  private static final String TEXT_WITH_TAGS = "<p>%s</p>";

  @Override
  public CommentListResponse toCommentListResponse(PostComment postComments) {
    var commentListResponse = new CommentListResponse();
    commentListResponse.setId(postComments.getId());
    commentListResponse.setTimestamp(postComments.getTime().toEpochSecond(UTC));
    commentListResponse.setText(format(TEXT_WITH_TAGS, postComments.getText()));
    commentListResponse.setUser(new AuthorOfComment(postComments.getAuthor().getId(),
        postComments.getAuthor().getName(), postComments.getAuthor().getPhoto()));
    return commentListResponse;
  }
}
