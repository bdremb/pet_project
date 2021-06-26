package com.diplom.skillbox.blog_driver.api.response;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class PostSpecificResponse {
  private int id;
  private long timestamp;
  private boolean active;
  private UserResponse user;
  private String title;
  private String text;
  private int likeCount;
  private int dislikeCount;
  private int viewCount;
  private List<CommentListResponse> comments;
  private List<String> tags;
  private boolean result;
  private Map<String, String> errors;
}
