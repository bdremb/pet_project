package com.diplom.skillbox.blog_driver.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class CommentListResponse {
  private int id;
  private long timestamp;
  private String text;
  private AuthorOfComment user;

  @Data
  @AllArgsConstructor
  public static class AuthorOfComment {
    private int id;
    private String name;
    private String photo;
  }
}
