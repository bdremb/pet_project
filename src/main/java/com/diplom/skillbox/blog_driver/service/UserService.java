package com.diplom.skillbox.blog_driver.service;

import com.diplom.skillbox.blog_driver.api.request.PhotoRequest;
import com.diplom.skillbox.blog_driver.api.request.UserProfileRequest;
import com.diplom.skillbox.blog_driver.api.response.ErrorsResponse;
import com.diplom.skillbox.blog_driver.exception.MaxImageSizeException;
import com.diplom.skillbox.blog_driver.exception.NotFoundUserByEmailException;
import com.diplom.skillbox.blog_driver.exception.UnsupportedImageFormatException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

public interface UserService {
  ErrorsResponse changeNameAndPassword(UserProfileRequest profileRequest,
                                       Principal principal,
                                       HttpServletRequest request,
                                       HttpServletResponse response)
      throws NotFoundUserByEmailException;

  ErrorsResponse changeUserPhoto(PhotoRequest request, Principal principal)
      throws UnsupportedImageFormatException, NotFoundUserByEmailException, IOException, MaxImageSizeException;
}
