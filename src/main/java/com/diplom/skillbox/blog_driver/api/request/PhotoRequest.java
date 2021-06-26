package com.diplom.skillbox.blog_driver.api.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PhotoRequest {
  private MultipartFile photo;
  private String name;
  private String email;
  private String password;
  private int removePhoto;
}
