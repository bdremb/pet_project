package com.diplom.skillbox.blog_driver.service;

import com.diplom.skillbox.blog_driver.api.response.StatisticResponse;
import com.diplom.skillbox.blog_driver.exception.ClosedStatisticBlogException;
import com.diplom.skillbox.blog_driver.exception.NotFoundUserByEmailException;
import java.security.Principal;

public interface StatisticService {
  StatisticResponse getUserStatistic(Principal principal) throws NotFoundUserByEmailException;

  StatisticResponse getAllStatistic(Principal principal) throws NotFoundUserByEmailException, ClosedStatisticBlogException;
}
