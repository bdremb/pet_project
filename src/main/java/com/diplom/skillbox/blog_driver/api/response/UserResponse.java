package com.diplom.skillbox.blog_driver.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
  private int id;
  private String name;
  private String photo;
}
