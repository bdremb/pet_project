package com.diplom.skillbox.blog_driver.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorsResponse {
  private boolean result;
  private Map<String, String> errors;
}
