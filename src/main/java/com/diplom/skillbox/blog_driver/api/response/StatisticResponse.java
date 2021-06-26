package com.diplom.skillbox.blog_driver.api.response;

import lombok.Data;

@Data
public class StatisticResponse {
  private int postsCount;
  private long likesCount;
  private long dislikesCount;
  private int viewsCount;
  private long firstPublication;
}
