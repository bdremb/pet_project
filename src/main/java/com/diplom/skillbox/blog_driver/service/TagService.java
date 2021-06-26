package com.diplom.skillbox.blog_driver.service;

import com.diplom.skillbox.blog_driver.api.response.TagsListResponse;

public interface TagService {
  TagsListResponse getTagsListResponse(String query);
}
