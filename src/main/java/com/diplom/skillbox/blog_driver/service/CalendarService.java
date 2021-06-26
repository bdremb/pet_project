package com.diplom.skillbox.blog_driver.service;

import com.diplom.skillbox.blog_driver.api.response.CalendarResponse;

public interface CalendarService {
  CalendarResponse outputOfPublicationsToTheCalendar(int year);
}
