package com.diplom.skillbox.blog_driver.api.response;

import lombok.Data;
import java.util.List;

@Data
public class PostListResponse {
  private long count;
  private List<PostResponse> posts;
}
