package com.diplom.skillbox.blog_driver.service;

import static java.time.LocalDateTime.now;
import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import com.diplom.skillbox.blog_driver.api.response.CalendarResponse;
import com.diplom.skillbox.blog_driver.repository.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class CalendarServiceImpl implements CalendarService {
  private static final int YEAR_NOT_SPECIFIED = 0;
  private final PostRepository postRepository;

  public CalendarServiceImpl(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Override
  public CalendarResponse outputOfPublicationsToTheCalendar(int year) {
    return new CalendarResponse(postRepository.findYearsOfPosts(now(UTC)),
        postRepository.findDatesOfPostsByYear(year == YEAR_NOT_SPECIFIED ? now(UTC).getYear() : year)
            .stream().collect(groupingBy(String::toString, counting())));
  }
}
