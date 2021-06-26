package com.diplom.skillbox.blog_driver.mapper;

import com.diplom.skillbox.blog_driver.api.response.StatisticResponse;
import com.diplom.skillbox.blog_driver.model.Post;
import com.diplom.skillbox.blog_driver.model.PostVotes;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface StatisticMapper {
  StatisticMapper INSTANCE = Mappers.getMapper(StatisticMapper.class);

  StatisticResponse toStatisticResponse(List<Post> posts, List<PostVotes> postVotes);
}
