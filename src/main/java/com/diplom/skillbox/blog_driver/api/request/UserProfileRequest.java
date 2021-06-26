package com.diplom.skillbox.blog_driver.api.request;

import lombok.Data;

@Data
public class UserProfileRequest {
  private String name;
  private String email;
  private String password;
  private int removePhoto;
  private String photo;
}
