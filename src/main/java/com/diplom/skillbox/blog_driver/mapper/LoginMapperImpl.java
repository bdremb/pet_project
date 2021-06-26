package com.diplom.skillbox.blog_driver.mapper;

import com.diplom.skillbox.blog_driver.api.response.LoginResponse;
import com.diplom.skillbox.blog_driver.api.response.UserLoginResponse;
import com.diplom.skillbox.blog_driver.model.User;

public class LoginMapperImpl implements LoginMapper {
  @Override
  public LoginResponse toLoginResponse(User user, int moderationCount) {
    var userResponse = new UserLoginResponse();
    userResponse.setEmail(user.getEmail());
    userResponse.setName(user.getName());
    userResponse.setModeration(user.getIsModerator());
    userResponse.setId(user.getId());
    userResponse.setSettings(true);
    userResponse.setModerationCount(moderationCount);
    userResponse.setPhoto(user.getPhoto());
    return new LoginResponse(true, userResponse);
  }
}
