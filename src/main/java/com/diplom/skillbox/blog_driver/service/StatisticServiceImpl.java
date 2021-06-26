package com.diplom.skillbox.blog_driver.service;

import static com.diplom.skillbox.blog_driver.enums.GlobalSettingsCode.STATISTICS_IS_PUBLIC;

import com.diplom.skillbox.blog_driver.api.response.StatisticResponse;
import com.diplom.skillbox.blog_driver.enums.ModerationStatus;
import com.diplom.skillbox.blog_driver.exception.ClosedStatisticBlogException;
import com.diplom.skillbox.blog_driver.exception.NotFoundUserByEmailException;
import com.diplom.skillbox.blog_driver.mapper.StatisticMapper;
import com.diplom.skillbox.blog_driver.model.Post;
import com.diplom.skillbox.blog_driver.model.User;
import com.diplom.skillbox.blog_driver.repository.GlobalSettingsRepository;
import com.diplom.skillbox.blog_driver.repository.PostRepository;
import com.diplom.skillbox.blog_driver.repository.PostVotesRepository;
import com.diplom.skillbox.blog_driver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.List;

@Service
public class StatisticServiceImpl implements StatisticService {
  final Logger logger = LoggerFactory.getLogger(StatisticServiceImpl.class);
  private static final byte IS_ACTIVE = 1;
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final PostVotesRepository postVotesRepository;
  private final GlobalSettingsRepository settingsRepository;

  public StatisticServiceImpl(PostRepository postRepository,
                              UserRepository userRepository,
                              PostVotesRepository postVotesRepository,
                              GlobalSettingsRepository settingsRepository) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
    this.postVotesRepository = postVotesRepository;
    this.settingsRepository = settingsRepository;
  }

  @Override
  public StatisticResponse getUserStatistic(Principal principal)
      throws NotFoundUserByEmailException {
    List<Post> posts = postRepository
        .findPostsByUserEmailAndIsActiveAndModerationStatusOrderByTime(principal.getName(),
            IS_ACTIVE, ModerationStatus.ACCEPTED);
    logger.info("Get user statistics.");
    return StatisticMapper.INSTANCE.toStatisticResponse(posts,
        postVotesRepository.findAllByUser(getUser(principal)));
  }

  @Override
  public StatisticResponse getAllStatistic(Principal principal)
      throws NotFoundUserByEmailException, ClosedStatisticBlogException {
    if (settingsRepository.getByCode(STATISTICS_IS_PUBLIC.toString()).getValue().equals(String.valueOf(false))
        && !getUser(principal).getIsModerator()) {
      throw new ClosedStatisticBlogException("Statistics is closed.");
    }
    logger.info("Get all statistics.");
    return StatisticMapper.INSTANCE.toStatisticResponse(postRepository.findAllPostsOrderByTime(),
        postVotesRepository.findAll());
  }

  private User getUser(Principal principal) throws NotFoundUserByEmailException {
    return userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new NotFoundUserByEmailException("User not found by email"));
  }
}
