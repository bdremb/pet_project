package com.diplom.skillbox.blog_driver.mapper;

import com.diplom.skillbox.blog_driver.api.response.LoginResponse;
import com.diplom.skillbox.blog_driver.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LoginMapper {

  LoginMapper INSTANCE = Mappers.getMapper(LoginMapper.class);

  LoginResponse toLoginResponse(User user, int moderationCount);
}
