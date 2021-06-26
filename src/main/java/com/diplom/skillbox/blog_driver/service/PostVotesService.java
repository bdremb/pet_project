package com.diplom.skillbox.blog_driver.service;

import java.security.Principal;
import java.util.Map;

public interface PostVotesService {
  Map<String, Boolean> addLike(Map<String, Integer> request, Principal principal);

  Map<String, Boolean> addDislike(Map<String, Integer> request, Principal principal);
}
