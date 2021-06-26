package com.diplom.skillbox.blog_driver.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
  private boolean result;
  @JsonProperty("user")
  private UserLoginResponse userLoginResponse;
}
