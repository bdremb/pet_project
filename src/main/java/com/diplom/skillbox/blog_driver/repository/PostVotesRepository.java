package com.diplom.skillbox.blog_driver.repository;

import com.diplom.skillbox.blog_driver.model.PostVotes;
import com.diplom.skillbox.blog_driver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface PostVotesRepository extends JpaRepository<PostVotes, Integer> {
  @Transactional
  Optional<PostVotes> findByUserIdAndPostId(int userId, int postId);

  List<PostVotes> findAllByUser(User user);
}
