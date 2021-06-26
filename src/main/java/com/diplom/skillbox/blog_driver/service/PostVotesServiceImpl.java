package com.diplom.skillbox.blog_driver.service;

import static java.time.LocalDateTime.now;
import static java.time.ZoneOffset.UTC;

import com.diplom.skillbox.blog_driver.model.PostVotes;
import com.diplom.skillbox.blog_driver.model.User;
import com.diplom.skillbox.blog_driver.repository.PostVotesRepository;
import com.diplom.skillbox.blog_driver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PostVotesServiceImpl implements PostVotesService {
  final Logger logger = LoggerFactory.getLogger(PostVotesServiceImpl.class);
  private static final String POST_ID = "post_id";
  private static final byte LIKE = 1;
  private static final byte DISLIKE = -1;
  private static final byte NO_LIKES = 0;
  private final PostVotesRepository postVotesRepository;
  private final UserRepository userRepository;

  public PostVotesServiceImpl(PostVotesRepository postVotesRepository,
                              UserRepository userRepository) {
    this.postVotesRepository = postVotesRepository;
    this.userRepository = userRepository;
  }

  @Override
  public Map<String, Boolean> addLike(Map<String, Integer> request,
                                      Principal principal) {
    return postVotesResponse(LIKE, principal, request.get(POST_ID));
  }

  @Override
  public Map<String, Boolean> addDislike(Map<String, Integer> request,
                                         Principal principal) {
    return postVotesResponse(DISLIKE, principal, request.get(POST_ID));
  }

  private HashMap<String, Boolean> postVotesResponse(byte like, Principal principal, int postId) {
    var response = new HashMap<String, Boolean>();
    var user = userRepository.findByEmail(principal.getName()).orElseThrow();
    var postVotes = postVotesRepository.findByUserIdAndPostId(user.getId(), postId)
        .orElse(new PostVotes());
    if (postVotes.getValue() == like && postVotes.getPostId() == postId
        && postVotes.getUser().equals(user)) {
      logger.error("PostVotes value = {} was not added to Post with ID={}", like, postId);
      response.put("result", false);
      return response;
    } else {
      postVotes.setValue(NO_LIKES);
      updatePostVotes(postVotes, user, postId, like);
      postVotesRepository.saveAndFlush(postVotes);
      response.put("result", true);
    }
    return response;
  }

  private void updatePostVotes(PostVotes postVotes, User user, int postId, byte like) {
    postVotes.setPostId(postId);
    postVotes.setUser(user);
    postVotes.setTime(now(UTC));
    if (postVotes.getPostId() == postId && postVotes.getUser().equals(user)) {
      postVotes.setValue(like);
      logger.info("Set PostVotes value = {} to the Post with ID={}", like, postId);
    }
  }
}