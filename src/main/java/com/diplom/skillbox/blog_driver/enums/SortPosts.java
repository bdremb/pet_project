package com.diplom.skillbox.blog_driver.enums;

import org.springframework.data.domain.Sort;

public enum SortPosts {
  SORT_BY_TIME_ASC(Sort.by("time").ascending()),
  SORT_BY_TIME_DESC(Sort.by("time").descending()),
  SORT_BY_COMMENTS(Sort.by("commentCount").descending()),
  SORT_BY_LIKE(Sort.by("likeCount").descending());

  private final Sort sort;

  SortPosts(Sort sort) {
    this.sort = sort;
  }

  public Sort getSortMethod() {
    return sort;
  }
}
