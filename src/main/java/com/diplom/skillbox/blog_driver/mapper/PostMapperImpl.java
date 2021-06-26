package com.diplom.skillbox.blog_driver.mapper;

import static java.lang.Math.min;
import static java.time.ZoneOffset.UTC;

import com.diplom.skillbox.blog_driver.api.response.PostResponse;
import com.diplom.skillbox.blog_driver.api.response.UserResponse;
import com.diplom.skillbox.blog_driver.model.Post;

public class PostMapperImpl implements PostMapper {
  private static final int MAX_ANNOUNCE_LENGTH = 150;
  private static final int LIKE = 1;
  private static final String HTML_TAG = "\\<.*?>";
  private static final String LINE_BREAK = "&nbsp;";
  private static final String SPACE = " ";
  private static final String ANNOUNCE_TEXT = "%s...";

  @Override
  public PostResponse toPostResponse(Post post) {
    var postResponse = new PostResponse();
    String postText = post.getText().replaceAll(HTML_TAG, SPACE);
    int likeCount = (int) post.getPostVotes().stream()
        .filter(postVotes -> postVotes.getValue() == LIKE).count();
    postResponse.setId(post.getId());
    postResponse.setTimestamp(post.getTime().toEpochSecond(UTC));
    postResponse.setUser(new UserResponse(post.getUser().getId(),
        post.getUser().getName(), post.getUser().getPhoto()));
    postResponse.setTitle(post.getTitle());
    postResponse.setAnnounce(String.format(ANNOUNCE_TEXT,
        postText.substring(0, min(postText.length(), MAX_ANNOUNCE_LENGTH)))
        .replace(LINE_BREAK, SPACE));
    postResponse.setLikeCount(likeCount);
    postResponse.setDislikeCount(post.getPostVotes().size() - likeCount);
    postResponse.setCommentCount(post.getPostComments().size());
    postResponse.setViewCount(post.getViewCount());
    return postResponse;
  }
}
