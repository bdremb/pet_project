package com.diplom.skillbox.blog_driver.mapper;

import com.diplom.skillbox.blog_driver.api.request.PostRequest;
import com.diplom.skillbox.blog_driver.api.response.PostSpecificResponse;
import com.diplom.skillbox.blog_driver.enums.ModerationStatus;
import com.diplom.skillbox.blog_driver.model.Post;
import com.diplom.skillbox.blog_driver.model.Tag;
import com.diplom.skillbox.blog_driver.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Mapper
public interface PostSpecificMapper {

  PostSpecificMapper INSTANCE = Mappers.getMapper(PostSpecificMapper.class);

  PostSpecificResponse toPostSpecificResponse(Post post);

  Post toPost(List<Tag> tags, PostRequest postRequest, Principal principal,
              User user, ModerationStatus moderationStatus);

  Post toUpdatePost(List<Tag> tags, Post post, PostRequest postRequest,
                    ModerationStatus moderationStatus);

  PostSpecificResponse toPostSpecificResponseWithErrors(Map<String, String> errors);
}
