package com.diplom.skillbox.blog_driver.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class TagsListResponse {
  private List<TagResponse> tags;

  @Data
  @AllArgsConstructor
  public static class TagResponse {
    private String name;
    private float weight;
  }
}
