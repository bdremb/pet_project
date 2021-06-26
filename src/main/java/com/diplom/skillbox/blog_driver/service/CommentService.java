package com.diplom.skillbox.blog_driver.service;

import com.diplom.skillbox.blog_driver.api.request.CommentRequest;
import com.diplom.skillbox.blog_driver.api.response.CommentResponse;
import com.diplom.skillbox.blog_driver.exception.NotFoundPostByIdException;
import com.diplom.skillbox.blog_driver.exception.NotFoundUserByEmailException;
import java.security.Principal;

public interface CommentService {
  CommentResponse savePostComment(CommentRequest commentRequest, Principal principal)
      throws NotFoundUserByEmailException, NotFoundPostByIdException;
}
