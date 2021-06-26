package com.diplom.skillbox.blog_driver.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChangePostStatusRequest {
  @JsonProperty("post_id")
  private int postId;
  private String decision;
}
