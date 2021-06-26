package com.diplom.skillbox.blog_driver.service;

import com.diplom.skillbox.blog_driver.api.request.ChangePasswordRequest;
import com.diplom.skillbox.blog_driver.api.request.LoginRequest;
import com.diplom.skillbox.blog_driver.api.request.RegistrationRequest;
import com.diplom.skillbox.blog_driver.api.response.ErrorsResponse;
import com.diplom.skillbox.blog_driver.api.response.LoginResponse;
import com.diplom.skillbox.blog_driver.api.response.LogoutResponse;
import com.diplom.skillbox.blog_driver.exception.ClosedRegistrationException;
import com.diplom.skillbox.blog_driver.exception.NotFoundUserByEmailException;
import com.diplom.skillbox.blog_driver.exception.NotValidatePasswordException;
import org.springframework.web.bind.annotation.RequestBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Map;

public interface AuthService {
  LoginResponse login(@RequestBody LoginRequest loginRequest) throws NotFoundUserByEmailException, NotValidatePasswordException;

  LoginResponse check(Principal principal) throws NotFoundUserByEmailException;

  LogoutResponse logout(HttpServletRequest request,
                        HttpServletResponse response);

  ErrorsResponse registration(RegistrationRequest registrationRequest)
      throws ClosedRegistrationException;

  Map<String, Boolean> restorePassword(Map<String, String> request);

  ErrorsResponse changePassword(ChangePasswordRequest changePasswordRequest);
}