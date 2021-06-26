package com.diplom.skillbox.blog_driver.service;

import com.diplom.skillbox.blog_driver.api.request.ChangePostStatusRequest;
import com.diplom.skillbox.blog_driver.api.request.PostRequest;
import com.diplom.skillbox.blog_driver.api.response.ErrorsResponse;
import com.diplom.skillbox.blog_driver.api.response.PostListResponse;
import com.diplom.skillbox.blog_driver.api.response.PostSpecificResponse;
import com.diplom.skillbox.blog_driver.exception.NotFoundPostByIdException;
import java.security.Principal;
import java.util.Map;

public interface PostService {
  PostListResponse getPosts(String mode, int offset, int limit);

  PostListResponse getPostsByQuery(String query, int offset, int limit);

  PostListResponse getPostsByDate(String date, int offset, int limit);

  PostListResponse getPostsByTag(String tag, int offset, int limit);

  PostSpecificResponse getPostById(int postId);

  PostListResponse getPostsForModerate(String status, int offset,
                                       int limit, Principal principal);

  PostListResponse getUserPosts(String status, int offset,
                                int limit, Principal principal);

  ErrorsResponse addPost(PostRequest postRequest, Principal principal);

  PostSpecificResponse updatePost(int postId, PostRequest postRequest)
      throws NotFoundPostByIdException;

  Map<String, Boolean> changePostStatus(ChangePostStatusRequest changePostStatusRequest,
                                        Principal principal);
}
