package com.diplom.skillbox.blog_driver.mapper;

import com.diplom.skillbox.blog_driver.api.response.PostResponse;
import com.diplom.skillbox.blog_driver.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {

  PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

  PostResponse toPostResponse(Post post);
}
