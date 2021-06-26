package com.diplom.skillbox.blog_driver.controller;

import static com.diplom.skillbox.blog_driver.enums.ErrorReason.ERROR_IMAGE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

import com.diplom.skillbox.blog_driver.api.request.ChangePostStatusRequest;
import com.diplom.skillbox.blog_driver.api.request.CommentRequest;
import com.diplom.skillbox.blog_driver.api.request.PhotoRequest;
import com.diplom.skillbox.blog_driver.api.request.UserProfileRequest;
import com.diplom.skillbox.blog_driver.api.response.CalendarResponse;
import com.diplom.skillbox.blog_driver.api.response.CommentResponse;
import com.diplom.skillbox.blog_driver.api.response.ErrorsResponse;
import com.diplom.skillbox.blog_driver.api.response.InitResponse;
import com.diplom.skillbox.blog_driver.api.response.StatisticResponse;
import com.diplom.skillbox.blog_driver.api.response.TagsListResponse;
import com.diplom.skillbox.blog_driver.exception.ClosedStatisticBlogException;
import com.diplom.skillbox.blog_driver.exception.MaxImageSizeException;
import com.diplom.skillbox.blog_driver.exception.NotFoundPostByIdException;
import com.diplom.skillbox.blog_driver.exception.NotFoundUserByEmailException;
import com.diplom.skillbox.blog_driver.exception.UnsupportedImageFormatException;
import com.diplom.skillbox.blog_driver.service.CalendarService;
import com.diplom.skillbox.blog_driver.service.CommentService;
import com.diplom.skillbox.blog_driver.service.GlobalSettingsServiceImpl;
import com.diplom.skillbox.blog_driver.service.ImageService;
import com.diplom.skillbox.blog_driver.service.PostService;
import com.diplom.skillbox.blog_driver.service.StatisticService;
import com.diplom.skillbox.blog_driver.service.TagService;
import com.diplom.skillbox.blog_driver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@ControllerAdvice
@RequestMapping("/api")
public class ApiGeneralController {
  final Logger logger = LoggerFactory.getLogger(ApiGeneralController.class);
  private final InitResponse initResponse;
  private final GlobalSettingsServiceImpl settingsService;
  private final TagService tagService;
  private final PostService postService;
  private final CommentService commentService;
  private final CalendarService calendarService;
  private final ImageService imageService;
  private final StatisticService statisticService;
  private final UserService userService;

  public ApiGeneralController(InitResponse initResponse,
                              GlobalSettingsServiceImpl settingsService,
                              TagService tagService,
                              PostService postService,
                              CommentService commentService,
                              CalendarService calendarService,
                              ImageService imageService,
                              StatisticService statisticService,
                              UserService userService) {
    this.initResponse = initResponse;
    this.settingsService = settingsService;
    this.tagService = tagService;
    this.postService = postService;
    this.commentService = commentService;
    this.calendarService = calendarService;
    this.imageService = imageService;
    this.statisticService = statisticService;
    this.userService = userService;
  }

  @GetMapping("/init")
  public ResponseEntity<InitResponse> init() {
    return ok(initResponse);
  }

  @GetMapping("/settings")
  public ResponseEntity<Map<String, Boolean>> getSettings() {
    return ok(settingsService.getGlobalSettings());
  }

  @PutMapping("/settings")
  @PreAuthorize("hasAuthority('user:moderate')")
  public void setSettings(@RequestBody Map<String, Boolean> requestSettings) {
    settingsService.setGlobalSettings(requestSettings);
  }

  @GetMapping("/tag")
  public ResponseEntity<TagsListResponse> getTags(
      @RequestParam(defaultValue = "%", required = false) String query) {
    return ok(tagService.getTagsListResponse(query));
  }

  @GetMapping("/calendar")
  public ResponseEntity<CalendarResponse> outputOfPublicationsToTheCalendar(
      @RequestParam(defaultValue = "0", required = false) int year) {
    return ok(calendarService.outputOfPublicationsToTheCalendar(year));
  }

  @PostMapping("/comment")
  @PreAuthorize("hasAuthority('user:read')")
  public ResponseEntity<CommentResponse> addComment(
      @RequestBody CommentRequest commentRequest, Principal principal) {
    try {
      return ok(commentService.savePostComment(commentRequest, principal));
    } catch (NotFoundUserByEmailException | NotFoundPostByIdException e) {
      logger.error(e.getMessage(), e);
      return badRequest().build();
    }
  }

  @PostMapping("/moderation")
  @PreAuthorize("hasAuthority('user:moderate')")
  public ResponseEntity<Map<String, Boolean>> changePostStatus(
      @RequestBody ChangePostStatusRequest changePostStatusRequest, Principal principal) {
    return ok(postService.changePostStatus(changePostStatusRequest, principal));
  }

  @PostMapping(value = "/image", consumes = {"multipart/form-data"})
  @PreAuthorize("hasAuthority('user:read')")
  public ResponseEntity storeImage(@RequestParam(value = "image") MultipartFile image) {
    var error = new HashMap<String, String>();
    try {
      return ok(imageService.storeImage(image));
    } catch (UnsupportedImageFormatException | IOException e) {
      logger.error(e.getMessage(), e);
      error.put(ERROR_IMAGE.get(), "Возможно отправлен файл не формата изображения jpg, png");
    } catch (MaxImageSizeException e) {
      logger.error(e.getMessage(), e);
      error.put(ERROR_IMAGE.get(), "Размер файла превышает допустимый размер 5 мегабайт");
    }
    return badRequest().body(new ErrorsResponse(error.isEmpty(), error));
  }

  @GetMapping("/statistics/my")
  @PreAuthorize("hasAuthority('user:read')")
  public ResponseEntity<StatisticResponse> getUserStatistic(Principal principal) {
    try {
      return ok(statisticService.getUserStatistic(principal));
    } catch (NotFoundUserByEmailException e) {
      logger.error(e.getMessage(), e);
      return notFound().build();
    }
  }

  @GetMapping("/statistics/all")
  public ResponseEntity<StatisticResponse> getAllStatistic(Principal principal) {
    try {
      return ok(statisticService.getAllStatistic(principal));
    } catch (NotFoundUserByEmailException e) {
      logger.error(e.getMessage(), e);
      return notFound().build();
    } catch (ClosedStatisticBlogException e) {
      logger.warn(e.getMessage(), e);
      return status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @PostMapping(value = "/profile/my", consumes = {"multipart/form-data"})
  @PreAuthorize("hasAuthority('user:read')")
  public ResponseEntity<ErrorsResponse> changeUserPhoto(
      @ModelAttribute("photo") PhotoRequest photoRequest, Principal principal) {
    var error = new HashMap<String, String>();
    try {
      return ok(userService.changeUserPhoto(photoRequest, principal));
    } catch (IOException | NotFoundUserByEmailException e) {
      logger.error(e.getMessage(), e);
    } catch (UnsupportedImageFormatException e) {
      logger.error(e.getMessage(), e);
      error.put(ERROR_IMAGE.get(), "Возможно отправлен файл не формата изображения jpg, png");
    } catch (MaxImageSizeException e) {
      logger.error(e.getMessage(), e);
      error.put(ERROR_IMAGE.get(), "Размер файла превышает допустимый размер 5 мегабайт");
    }
    return badRequest().body(new ErrorsResponse(error.isEmpty(), error));
  }

  @PostMapping(value = "/profile/my", consumes = {"application/json"})
  @PreAuthorize("hasAuthority('user:read')")
  public ResponseEntity<ErrorsResponse> changeUserProfile(
      @RequestBody UserProfileRequest userProfileRequest, Principal principal,
      HttpServletRequest request,
      HttpServletResponse response) {
    try {
      return ok(userService.changeNameAndPassword(userProfileRequest, principal,
          request, response));
    } catch (NotFoundUserByEmailException e) {
      logger.error(e.getMessage(), e);
      return badRequest().build();
    }
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorsResponse> handleMaxSizeException(
      MaxUploadSizeExceededException exc) {
    var errors = new HashMap<String, String>();
    errors.put(ERROR_IMAGE.get(), "Размер файла превышает допустимый размер");
    logger.error(exc.getMessage(), exc);
    return badRequest().body(new ErrorsResponse(errors.isEmpty(), errors));
  }
}
