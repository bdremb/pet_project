package com.diplom.skillbox.blog_driver.mapper;

import com.diplom.skillbox.blog_driver.api.response.PostListResponse;
import com.diplom.skillbox.blog_driver.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper
public interface PostListResponseMapper {

  PostListResponseMapper INSTANCE = Mappers.getMapper(PostListResponseMapper.class);

  PostListResponse toPostListResponse(Page<Post> posts);
}
