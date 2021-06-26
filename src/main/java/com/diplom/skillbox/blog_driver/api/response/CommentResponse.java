package com.diplom.skillbox.blog_driver.api.response;

import lombok.Data;
import java.util.Map;

@Data
public class CommentResponse {
  private boolean result;
  private Map<String, String> errors;
  private int id;
}
