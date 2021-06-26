package com.diplom.skillbox.blog_driver.mapper;

import static java.time.ZoneOffset.UTC;

import com.diplom.skillbox.blog_driver.api.response.StatisticResponse;
import com.diplom.skillbox.blog_driver.model.Post;
import com.diplom.skillbox.blog_driver.model.PostVotes;
import java.util.List;
import java.util.Optional;

public class StatisticMapperImpl implements StatisticMapper {
  private static final int LIKE = 1;
  private static final int DISLIKE = -1;
  private static final byte FIRST_ELEMENT = 0;

  @Override
  public StatisticResponse toStatisticResponse(List<Post> posts, List<PostVotes> postVotes) {
    var statisticResponse = new StatisticResponse();
    if (posts.isEmpty()) {
      statisticResponse.setPostsCount(0);
      statisticResponse.setFirstPublication(0);
      statisticResponse.setViewsCount(0);
    } else {
      statisticResponse.setPostsCount(Optional.of(posts.size()).orElse(0));
      statisticResponse.setFirstPublication(posts.get(FIRST_ELEMENT).getTime().toEpochSecond(UTC));
      statisticResponse.setViewsCount(posts.stream().mapToInt(Post::getViewCount).sum());
    }
    statisticResponse.setLikesCount(postVotes.stream()
        .filter(vote -> vote.getValue() == LIKE).count());
    statisticResponse.setDislikesCount(postVotes.stream()
        .filter(vote -> vote.getValue() == DISLIKE).count());
    return statisticResponse;
  }
}
