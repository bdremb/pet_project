package com.diplom.skillbox.blog_driver.service;

import static java.time.LocalDateTime.now;
import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toList;

import com.diplom.skillbox.blog_driver.api.response.TagsListResponse;
import com.diplom.skillbox.blog_driver.repository.TagRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class TagServiceImpl implements TagService {
  private final TagRepository tagRepository;

  public TagServiceImpl(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  public TagsListResponse getTagsListResponse(String query) {
    var localDateTime = now(UTC);
    return new TagsListResponse(tagRepository.findTagsByName(query, localDateTime).stream()
        .map(tag -> getTagResponse(tag.getName(), localDateTime)).collect(toList()));
  }

  private TagsListResponse.TagResponse getTagResponse(String tagName, LocalDateTime time) {
    float tagWeight = tagRepository.countTagsByName(tagName, time) / tagRepository.countAllTags(time);
    return new TagsListResponse.TagResponse(tagName, tagWeight);
  }
}
