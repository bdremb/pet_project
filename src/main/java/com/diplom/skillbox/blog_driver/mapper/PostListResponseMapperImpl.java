package com.diplom.skillbox.blog_driver.mapper;

import static java.util.stream.Collectors.toList;

import com.diplom.skillbox.blog_driver.api.response.PostListResponse;
import com.diplom.skillbox.blog_driver.model.Post;
import org.springframework.data.domain.Page;

public class PostListResponseMapperImpl implements PostListResponseMapper {

  @Override
  public PostListResponse toPostListResponse(Page<Post> posts) {
    var postListResponse = new PostListResponse();
    postListResponse.setPosts(
        posts.stream().map(PostMapper.INSTANCE::toPostResponse).collect(toList()));
    postListResponse.setCount(posts.getTotalElements());
    return postListResponse;
  }
}
