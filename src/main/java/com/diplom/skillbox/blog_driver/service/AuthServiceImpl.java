package com.diplom.skillbox.blog_driver.service;

import static com.diplom.skillbox.blog_driver.enums.ErrorReason.ERROR_CAPTCHA;
import static com.diplom.skillbox.blog_driver.enums.ErrorReason.ERROR_CODE;
import static com.diplom.skillbox.blog_driver.enums.ErrorReason.ERROR_EMAIL;
import static com.diplom.skillbox.blog_driver.enums.ErrorReason.ERROR_PASSWORD;
import static com.diplom.skillbox.blog_driver.enums.GlobalSettingsCode.MULTIUSER_MODE;
import static java.lang.String.valueOf;
import static java.time.ZoneOffset.UTC;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static org.springframework.security.crypto.bcrypt.BCrypt.checkpw;
import static org.springframework.security.crypto.bcrypt.BCrypt.gensalt;
import static org.springframework.security.crypto.bcrypt.BCrypt.hashpw;

import com.diplom.skillbox.blog_driver.api.request.ChangePasswordRequest;
import com.diplom.skillbox.blog_driver.api.request.LoginRequest;
import com.diplom.skillbox.blog_driver.api.request.RegistrationRequest;
import com.diplom.skillbox.blog_driver.api.response.ErrorsResponse;
import com.diplom.skillbox.blog_driver.api.response.LoginResponse;
import com.diplom.skillbox.blog_driver.api.response.LogoutResponse;
import com.diplom.skillbox.blog_driver.exception.ClosedRegistrationException;
import com.diplom.skillbox.blog_driver.exception.NotFoundCaptchaCodeBySecretCodeException;
import com.diplom.skillbox.blog_driver.exception.NotFoundUserByCodeException;
import com.diplom.skillbox.blog_driver.exception.NotFoundUserByEmailException;
import com.diplom.skillbox.blog_driver.exception.NotValidatePasswordException;
import com.diplom.skillbox.blog_driver.mapper.LoginMapper;
import com.diplom.skillbox.blog_driver.model.User;
import com.diplom.skillbox.blog_driver.repository.CaptchaCodeRepository;
import com.diplom.skillbox.blog_driver.repository.GlobalSettingsRepository;
import com.diplom.skillbox.blog_driver.repository.PostRepository;
import com.diplom.skillbox.blog_driver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
  final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
  @Value("${spring.mail.server.link}")
  private String serverLink;
  @Value("${user.photo.default}")
  private String userPhotoDefault;
  private static final int NO_POSTS_FOR_MODERATION = 0;
  private static final int LOG_ROUNDS = 12;
  private static final int MIN_PASSWORD_LENGTH = 6;
  private static final String USER_IS_FOUND = "result";
  private static final String REQUEST_EMAIL = "email";
  private static final String LINK_TO_RESTORE_PASSWORD = "Ссылка для восстановления пароля устарела."
      + " <a href=\"/login/restore-password\">Запросить ссылку снова</a>";
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final CaptchaCodeRepository codeRepository;
  private final GlobalSettingsRepository settingsRepository;
  private final MailService mailService;

  public AuthServiceImpl(AuthenticationManager authenticationManager,
                         UserRepository userRepository,
                         PostRepository postRepository,
                         CaptchaCodeRepository codeRepository,
                         GlobalSettingsRepository settingsRepository,
                         MailService mailService) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.postRepository = postRepository;
    this.codeRepository = codeRepository;
    this.settingsRepository = settingsRepository;
    this.mailService = mailService;
  }

  @Override
  public LoginResponse login(LoginRequest loginRequest)
      throws NotFoundUserByEmailException, NotValidatePasswordException {
    var user = getUserByEmail(loginRequest.getEmail());
    if (checkpw(loginRequest.getPassword(), user.getPassword())) {
      Authentication auth = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(auth);
      return getLoginResponse(user);
    } else {
      throw new NotValidatePasswordException("Incorrect password.");
    }
  }

  @Override
  public LoginResponse check(Principal principal) throws NotFoundUserByEmailException {
    try {
      return getLoginResponse(getUserByEmail(principal.getName()));
    } catch (NullPointerException e) {
      logger.info("{}. User is not logged in.", e.getMessage());
    }
    return new LoginResponse();
  }

  @Override
  public LogoutResponse logout(HttpServletRequest request,
                               HttpServletResponse response) {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    new SecurityContextLogoutHandler().logout(request, response, auth);
    logger.info("Logout User");
    return new LogoutResponse(true);
  }

  @Override
  public ErrorsResponse registration(RegistrationRequest registrationRequest)
      throws ClosedRegistrationException {
    if (settingsRepository.getByCode(MULTIUSER_MODE.toString()).getValue().equals(valueOf(false))) {
      throw new ClosedRegistrationException("Registration is closed");
    }
    var errors = getErrors(registrationRequest);
    if (errors.isEmpty()) {
      var user = new User();
      user.setEmail(registrationRequest.getEmail());
      user.setName(registrationRequest.getName());
      user.setPassword(hashpw(registrationRequest.getPassword(), gensalt(LOG_ROUNDS)));
      user.setRegTime(LocalDateTime.now(UTC));
      user.setPhoto(userPhotoDefault);
      userRepository.saveAndFlush(user);
    }
    return new ErrorsResponse(errors.isEmpty(), errors);
  }

  @Override
  public Map<String, Boolean> restorePassword(Map<String, String> request) {
    var response = new HashMap<String, Boolean>();
    var code = randomUUID().toString();
    String userEmail = request.get(REQUEST_EMAIL);
    response.put(USER_IS_FOUND, false);
    try {
      var userByEmail = getUserByEmail(userEmail);
      getUserByEmail(userEmail).setCode(code);
      mailService.sendEmail(userEmail, "Activation code", serverLink + code);
      userRepository.saveAndFlush(userByEmail);
      response.put(USER_IS_FOUND, true);
      logger.info("Email sent successfully");
    } catch (NotFoundUserByEmailException e) {
      logger.warn("Password was not restore. \n\t{}", e.getMessage());
    } catch (Exception e) {
      logger.error("Message with activation code was not sent \n\t {}", e.getMessage());
    }
    return response;
  }

  @Override
  public ErrorsResponse changePassword(ChangePasswordRequest changePasswordRequest) {
    var errors = new HashMap<String, String>();
    try {
      if (captchaMatches(changePasswordRequest)) {
        savePassword(changePasswordRequest);
        logger.info("Password was changed");
      } else {
        errors.put(ERROR_CAPTCHA.get(), "Код с картинки введен неверно.");
      }
    } catch (NotFoundUserByCodeException e) {
      errors.put(ERROR_CODE.get(), LINK_TO_RESTORE_PASSWORD);
      logger.warn(e.getMessage(), e);
    }
    return new ErrorsResponse(errors.isEmpty(), errors);
  }

  private void savePassword(ChangePasswordRequest changePasswordRequest)
      throws NotFoundUserByCodeException {
    var user = userRepository.findUserByCode(changePasswordRequest.getCode())
        .orElseThrow(() -> new NotFoundUserByCodeException("User was not found by code."));
    user.setPassword(hashpw(changePasswordRequest.getPassword(), gensalt(LOG_ROUNDS)));
    userRepository.saveAndFlush(user);
  }

  private com.diplom.skillbox.blog_driver.model.User getUserByEmail(String email)
      throws NotFoundUserByEmailException {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundUserByEmailException("Email was not found"));
  }

  private LoginResponse getLoginResponse(com.diplom.skillbox.blog_driver.model.User user) {
    if (user.getIsModerator()) {
      logger.info("Return LoginResponse from getLoginResponse with User isModerator.");
      return LoginMapper.INSTANCE.toLoginResponse(user,
          postRepository.findCountOfPostForModeration(user.getEmail()));
    }
    logger.info("Return LoginResponse from getLoginResponse");
    return LoginMapper.INSTANCE.toLoginResponse(user, NO_POSTS_FOR_MODERATION);
  }

  private boolean captchaMatches(ChangePasswordRequest changePasswordRequest) {
    var optionalCaptchaCode = codeRepository
        .getCaptchaCodeBySecretCode(changePasswordRequest.getCaptchaSecret());
    return optionalCaptchaCode.isPresent()
        && optionalCaptchaCode.get().getCode().equals(changePasswordRequest.getCaptcha());
  }

  private HashMap<String, String> getErrors(RegistrationRequest registrationRequest) {
    var errors = new HashMap<String, String>();
    try {
      var optionalCaptchaCode = ofNullable(codeRepository
          .getCaptchaCodeBySecretCode(registrationRequest.getCaptchaSecret())
          .orElseThrow(() -> new NotFoundCaptchaCodeBySecretCodeException("Incorrect captcha.")));
      if (optionalCaptchaCode.isPresent() && !optionalCaptchaCode.get().getCode()
          .equals(registrationRequest.getCaptcha())) {
        errors.put(ERROR_CAPTCHA.get(), "Код с картинки введен неверно.");
        logger.error("The code from the picture was entered incorrectly.");
      }
    } catch (NotFoundCaptchaCodeBySecretCodeException e) {
      errors.put(ERROR_CAPTCHA.get(), "CaptchaCode not found.");
      logger.error(e.toString());
    }
    if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
      errors.put(ERROR_EMAIL.get(), "Этот e-mail уже зарегистрирован.");
      logger.error("This e-mail is already registered.");
    }
    if (registrationRequest.getPassword().length() < MIN_PASSWORD_LENGTH) {
      errors.put(ERROR_PASSWORD.get(), "Пароль короче 6-ти символов.");
      logger.error("The password is shorter than 6 characters.");
    }
    return errors;
  }
}