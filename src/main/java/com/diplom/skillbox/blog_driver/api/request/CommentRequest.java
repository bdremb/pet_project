package com.diplom.skillbox.blog_driver.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommentRequest {
  @JsonProperty("parent_id")
  private int parentId = 0;
  @JsonProperty("post_id")
  private int postId;
  private String text;

}
