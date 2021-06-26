package com.diplom.skillbox.blog_driver.mapper;

import com.diplom.skillbox.blog_driver.api.response.CommentListResponse;
import com.diplom.skillbox.blog_driver.model.PostComment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentListMapper {

  CommentListMapper INSTANCE = Mappers.getMapper(CommentListMapper.class);

  CommentListResponse toCommentListResponse(PostComment postComments);
}
