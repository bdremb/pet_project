package com.diplom.skillbox.blog_driver.controller;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

import com.diplom.skillbox.blog_driver.api.request.PostRequest;
import com.diplom.skillbox.blog_driver.api.response.ErrorsResponse;
import com.diplom.skillbox.blog_driver.api.response.PostListResponse;
import com.diplom.skillbox.blog_driver.api.response.PostSpecificResponse;
import com.diplom.skillbox.blog_driver.exception.NotFoundPostByIdException;
import com.diplom.skillbox.blog_driver.service.PostService;
import com.diplom.skillbox.blog_driver.service.PostVotesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.lang.module.FindException;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {
  final Logger logger = LoggerFactory.getLogger(ApiPostController.class);
  private final PostService postService;
  private final PostVotesService postVotesService;

  public ApiPostController(PostService postService,
                           PostVotesService postVotesService) {
    this.postService = postService;
    this.postVotesService = postVotesService;
  }

  @GetMapping("")
  public ResponseEntity<PostListResponse> listOfPosts(
      @RequestParam(defaultValue = " ", required = false) String mode,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "10") int limit) {
    return ok(postService.getPosts(mode, offset, limit));
  }

  @GetMapping("/search")
  public ResponseEntity<PostListResponse> getPostsByQuery(
      @RequestParam(defaultValue = " ", required = false) String query,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "10") int limit) {
    return ok(postService.getPostsByQuery(query, offset, limit));
  }

  @GetMapping("/byDate")
  public ResponseEntity<PostListResponse> getPostsByDate(
      @RequestParam String date,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "10") int limit) {
    return ok(postService.getPostsByDate(date, offset, limit));
  }

  @GetMapping("/byTag")
  public ResponseEntity<PostListResponse> getPostsByTag(
      @RequestParam String tag,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "10") int limit) {
    return ok(postService.getPostsByTag(tag, offset, limit));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PostSpecificResponse> getPostById(@PathVariable int id) {
    ResponseEntity<PostSpecificResponse> responseEntity;
    try {
      responseEntity = ok(postService.getPostById(id));
    } catch (FindException e) {
      logger.error("Post by ID = {} is not found", id);
      responseEntity = notFound().build();
    }
    return responseEntity;
  }

  @GetMapping("/moderation")
  @PreAuthorize("hasAuthority('user:moderate')")
  public ResponseEntity<PostListResponse> listOfPostsForModerate(
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "10") int limit, Principal principal) {
    return ok(postService.getPostsForModerate(status, offset, limit, principal));
  }

  @GetMapping("/my")
  @PreAuthorize("hasAuthority('user:read')")
  public ResponseEntity<PostListResponse> listOfMyPosts(
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "10") int limit, Principal principal) {
    return ok(postService.getUserPosts(status, offset, limit, principal));
  }

  @PostMapping("")
  @PreAuthorize("hasAuthority('user:read')")
  public ResponseEntity<ErrorsResponse> addPost(
      @RequestBody PostRequest postRequest, Principal principal) {
    return ok(postService.addPost(postRequest, principal));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('user:read')")
  public ResponseEntity<PostSpecificResponse> updatePostById(
      @PathVariable int id,
      @RequestBody PostRequest postRequest) {
    ResponseEntity<PostSpecificResponse> responseEntity;
    try {
      responseEntity = ok(postService.updatePost(id, postRequest));
    } catch (NotFoundPostByIdException e) {
      logger.error(e.getMessage(), e);
      responseEntity = notFound().build();
    }
    return responseEntity;
  }

  @PostMapping("/like")
  @PreAuthorize("hasAuthority('user:read')")
  public ResponseEntity<Map<String, Boolean>> addLike(
      @RequestBody Map<String, Integer> request, Principal principal) {
    return ok(postVotesService.addLike(request, principal));
  }

  @PostMapping("/dislike")
  @PreAuthorize("hasAuthority('user:read')")
  public ResponseEntity<Map<String, Boolean>> addDisLike(
      @RequestBody Map<String, Integer> request, Principal principal) {
    return ok(postVotesService.addDislike(request, principal));
  }
}
