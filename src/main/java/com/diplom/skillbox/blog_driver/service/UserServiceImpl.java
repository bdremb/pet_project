package com.diplom.skillbox.blog_driver.service;

import static com.diplom.skillbox.blog_driver.enums.ErrorReason.ERROR_EMAIL;
import static com.diplom.skillbox.blog_driver.enums.ErrorReason.ERROR_NAME;
import static com.diplom.skillbox.blog_driver.enums.ErrorReason.ERROR_PASSWORD;
import static org.springframework.security.crypto.bcrypt.BCrypt.gensalt;
import static org.springframework.security.crypto.bcrypt.BCrypt.hashpw;

import com.diplom.skillbox.blog_driver.api.request.PhotoRequest;
import com.diplom.skillbox.blog_driver.api.request.UserProfileRequest;
import com.diplom.skillbox.blog_driver.api.response.ErrorsResponse;
import com.diplom.skillbox.blog_driver.exception.MaxImageSizeException;
import com.diplom.skillbox.blog_driver.exception.NotFoundUserByEmailException;
import com.diplom.skillbox.blog_driver.exception.UnsupportedImageFormatException;
import com.diplom.skillbox.blog_driver.model.User;
import com.diplom.skillbox.blog_driver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
  final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
  @Value("${upload.photoPath}")
  private String uploadPhotoPath;
  @Value("${user.photo.default}")
  private String userPhotoDefault;
  @Value("${upload.max-avatar-width}")
  private int maxAvatarWidth;
  private static final int MIN_PASSWORD_LENGTH = 6;
  private static final int YES = 1;
  private static final int LOG_ROUNDS = 12;
  private static final String NAME_PATTERN = "^[a-zA-Zа-яА-Я][a-zA-Zа-яА-Я0-9-_\\.]{1,20}$";
  private final UserRepository userRepository;
  private final ImageService imageService;
  private final AuthService authService;

  public UserServiceImpl(UserRepository userRepository,
                         ImageService imageService,
                         AuthService authService) {
    this.userRepository = userRepository;
    this.imageService = imageService;
    this.authService = authService;
  }

  @Override
  public ErrorsResponse changeUserPhoto(PhotoRequest request, Principal principal)
      throws UnsupportedImageFormatException, NotFoundUserByEmailException,
      IOException, MaxImageSizeException {
    User currentUser = getUserByEmail(principal.getName());
    var errors = new HashMap<String, String>();
    currentUser.setPhoto(imageService.getUrlImageCloudinary(request.getPhoto(),
        maxAvatarWidth, uploadPhotoPath));
    checkPassword(currentUser, errors, request.getPassword());
    if (errors.isEmpty()) {
      userRepository.saveAndFlush(currentUser);
    }
    return new ErrorsResponse(errors.isEmpty(), errors);
  }

  @Override
  public ErrorsResponse changeNameAndPassword(UserProfileRequest profileRequest,
                                              Principal principal,
                                              HttpServletRequest request,
                                              HttpServletResponse response)
      throws NotFoundUserByEmailException {
    var errors = new HashMap<String, String>();
    User currentUser = getUserByEmail(principal.getName());
    checkErrors(profileRequest, errors, currentUser);
    if (profileRequest.getRemovePhoto() == YES) {
      currentUser.setPhoto(userPhotoDefault);
      logger.info("User was deleted photo");
    }
    checkPassword(currentUser, errors, profileRequest.getPassword());
    if (errors.isEmpty()) {
      userRepository.saveAndFlush(currentUser);
      if (!principal.getName().equals(currentUser.getEmail())) {
        authService.logout(request, response);
        logger.info("User email is changed");
      }
    }
    return new ErrorsResponse(errors.isEmpty(), errors);
  }

  private void checkErrors(UserProfileRequest profileRequest,
                           Map<String, String> errors,
                           User currentUser) {
    var name = profileRequest.getName();
    var email = profileRequest.getEmail();
    Optional<User> user = userRepository.findByEmail(email);
    if (name != null && !name.matches(NAME_PATTERN)) {
      errors.put(ERROR_NAME.get(), "Имя указано неверно");
      logger.warn("Name error.");
    } else if (name == null) {
      currentUser.setName(currentUser.getName());
    } else {
      currentUser.setName(name);
    }
    if (email != null && user.isPresent() && !user.get().getEmail().equals(email)) {
      errors.put(ERROR_EMAIL.get(), "Этот e-mail уже зарегистрирован");
      logger.warn("This email is present. Email was not changed.");
    } else if (email == null) {
      currentUser.setEmail(currentUser.getEmail());
    } else {
      currentUser.setEmail(email);
    }
  }

  private void checkPassword(User currentUser, HashMap<String, String> errors, String password) {
    if (password != null && password.length() < MIN_PASSWORD_LENGTH) {
      errors.put(ERROR_PASSWORD.get(), "Пароль короче 6-ти символов");
    } else if (password != null) {
      currentUser.setPassword(hashpw(password, gensalt(LOG_ROUNDS)));
    } else {
      currentUser.setPassword(currentUser.getPassword());
    }
  }

  private com.diplom.skillbox.blog_driver.model.User getUserByEmail(String email)
      throws NotFoundUserByEmailException {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundUserByEmailException("Email was not found"));
  }
}
