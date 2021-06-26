package com.diplom.skillbox.blog_driver.controller;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

import com.diplom.skillbox.blog_driver.api.request.ChangePasswordRequest;
import com.diplom.skillbox.blog_driver.api.request.LoginRequest;
import com.diplom.skillbox.blog_driver.api.request.RegistrationRequest;
import com.diplom.skillbox.blog_driver.api.response.CaptchaResponse;
import com.diplom.skillbox.blog_driver.api.response.ErrorsResponse;
import com.diplom.skillbox.blog_driver.api.response.LoginResponse;
import com.diplom.skillbox.blog_driver.api.response.LogoutResponse;
import com.diplom.skillbox.blog_driver.exception.ClosedRegistrationException;
import com.diplom.skillbox.blog_driver.exception.NotFoundUserByEmailException;
import com.diplom.skillbox.blog_driver.exception.NotValidatePasswordException;
import com.diplom.skillbox.blog_driver.service.AuthService;
import com.diplom.skillbox.blog_driver.service.CaptchaCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
  final Logger logger = LoggerFactory.getLogger(ApiAuthController.class);
  private final AuthService authService;
  private final CaptchaCodeService captchaCodeService;

  public ApiAuthController(AuthService authService,
                           CaptchaCodeService captchaCodeService) {
    this.authService = authService;
    this.captchaCodeService = captchaCodeService;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
      @RequestBody LoginRequest loginRequest) {
    try {
      return ok(authService.login(loginRequest));
    } catch (NotFoundUserByEmailException | NotValidatePasswordException e) {
      logger.error(e.getMessage());
      return ok(new LoginResponse());
    }
  }

  @GetMapping("/check")
  public ResponseEntity<LoginResponse> check(Principal principal) {
    try {
      return ok(authService.check(principal));
    } catch (NotFoundUserByEmailException e) {
      logger.error(e.getMessage());
      return ok(new LoginResponse());
    }
  }

  @GetMapping("/logout")
  public ResponseEntity<LogoutResponse> logout(HttpServletRequest request,
                                               HttpServletResponse response) {
    return ok(authService.logout(request, response));
  }

  @PostMapping("/register")
  public ResponseEntity<ErrorsResponse> registration(
      @RequestBody RegistrationRequest registrationRequest) {
    ResponseEntity<ErrorsResponse> responseEntity;
    try {
      responseEntity = ok(authService.registration(registrationRequest));
      logger.info("User registered successfully");
    } catch (ClosedRegistrationException e) {
      logger.error(e.getMessage());
      responseEntity = notFound().build();
    }
    return responseEntity;
  }

  @GetMapping("/captcha")
  public ResponseEntity<CaptchaResponse> captchaGenerate() {
    return ok(captchaCodeService.captchaGenerator());
  }

  @PostMapping("/restore")
  public ResponseEntity<Map<String, Boolean>> restorePassword(
      @RequestBody Map<String, String> request) {
    return ok(authService.restorePassword(request));
  }

  @PostMapping("/password")
  public ResponseEntity<ErrorsResponse> changePassword(
      @RequestBody ChangePasswordRequest changePasswordRequest) {
    return ok(authService.changePassword(changePasswordRequest));
  }
}

