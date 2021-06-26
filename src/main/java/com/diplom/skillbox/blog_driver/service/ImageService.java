package com.diplom.skillbox.blog_driver.service;

import com.diplom.skillbox.blog_driver.exception.MaxImageSizeException;
import com.diplom.skillbox.blog_driver.exception.UnsupportedImageFormatException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ImageService {
  String storeImage(MultipartFile file) throws UnsupportedImageFormatException, IOException, MaxImageSizeException;

  String getUrlImageCloudinary(MultipartFile image, double width, String path)
      throws UnsupportedImageFormatException, IOException, MaxImageSizeException;
}
