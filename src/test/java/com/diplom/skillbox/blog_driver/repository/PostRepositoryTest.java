package com.diplom.skillbox.blog_driver.repository;

import com.diplom.skillbox.blog_driver.enums.ModerationStatus;
import com.diplom.skillbox.blog_driver.model.Post;
import com.diplom.skillbox.blog_driver.model.PostComment;
import com.diplom.skillbox.blog_driver.model.PostVotes;
import com.diplom.skillbox.blog_driver.model.Tag;
import com.diplom.skillbox.blog_driver.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static com.diplom.skillbox.blog_driver.enums.SortPosts.SORT_BY_COMMENTS;
import static com.diplom.skillbox.blog_driver.enums.SortPosts.SORT_BY_LIKE;
import static com.diplom.skillbox.blog_driver.enums.SortPosts.SORT_BY_TIME_DESC;
import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.data.domain.PageRequest.of;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
class PostRepositoryTest {
  @Autowired
  private PostRepository postRepository;

  @Test
  @DisplayName("Get Posts sorted by mode = recent")
  void getRecentPosts() {
    var pageable = of(0, 3, SORT_BY_TIME_DESC.getSortMethod());
    var postList = postRepository.findAllPosts(now(), pageable).toList();
    assertTrue(postList.get(0).getTime().isAfter(postList.get(1).getTime()));
    assertTrue(postList.get(1).getTime().isAfter(postList.get(2).getTime()));
    assertTrue(postList.get(2).getTime().isBefore(postList.get(0).getTime()));
  }

  @Test
  @DisplayName("Get Posts sorted by mode = popular")
  void getPopularPosts() {
    var pageable = of(0, 3, SORT_BY_COMMENTS.getSortMethod());
    var postList = postRepository.findAllPosts(now(), pageable).toList();
    assertTrue(postList.get(0).getPostComments().size() >= postList.get(1).getPostComments().size());
    assertTrue(postList.get(1).getPostComments().size() >= postList.get(2).getPostComments().size());
    assertTrue(postList.get(2).getPostComments().size() < postList.get(0).getPostComments().size());
  }

  @Test
  @DisplayName("Get Posts sorted by mode = best")
  void getBestPosts() {
    var pageable = of(0, 3, SORT_BY_LIKE.getSortMethod());
    var postList = postRepository.findAllPosts(now(), pageable).toList();
    long postLike1 = postList.get(0).getPostVotes().stream().filter(postVotes -> postVotes.getValue() == 1).count()
        - postList.get(0).getPostVotes().stream().filter(postVotes -> postVotes.getValue() == -1).count();
    long postLike3 = postList.get(2).getPostVotes().stream().filter(postVotes -> postVotes.getValue() == 1).count()
        - postList.get(2).getPostVotes().stream().filter(postVotes -> postVotes.getValue() == -1).count();
    assertTrue(postLike1 > postLike3);
  }

  @Test
  @DisplayName("Get Posts by query")
  void getPostByQuery() {
    var post = getPost();
    post.setTitle("Title Test $@# PHP 2");
    postRepository.saveAndFlush(post);
    var postList = postRepository.findPostsByQuery("$@#", now(), of(0, 3)).toList();
    assertEquals(post.getTitle(), postList.get(0).getTitle());
  }

  @Test
  @DisplayName("Get Posts by date")
  void getPostsByDate() {
    var post = getPost();
    post.setTime(LocalDateTime.of(1978, 3, 13, 13, 0));
    postRepository.saveAndFlush(post);
    var postList =
        postRepository.findPostsByDate(LocalDate.of(1978, 3, 13), of(0, 3)).toList();
    assertEquals(post.getTime(), postList.get(0).getTime());
  }

  @Test
  @DisplayName("Get Posts by Tag")
  void getPostsByTag() {
    var post = getPost();
    postRepository.saveAndFlush(post);
    var postList = postRepository.findPostsByTags("&*&", now(), of(0, 3)).toList();
    assertEquals(post.getTags().get(0), postList.get(0).getTags().get(0));
  }

  private Post getPost() {
    var post = new Post();
    var postVotes = new PostVotes();
    var postVotesList = new ArrayList<PostVotes>();
    var postComments = new ArrayList<PostComment>();
    var tags = new ArrayList<Tag>();
    postVotes.setValue((byte) 1);
    postVotes.setUser(getUserModerator());

    postVotesList.add(postVotes);

    tags.add(new Tag("&*&"));

    post.setIsActive((byte) 1);
    post.setModerationStatus(ModerationStatus.ACCEPTED);
    post.setText(String.format("Test post %d", 1));
    post.setTime(LocalDateTime.of(2020, 3, 11, 11, 0));
    post.setTitle(String.format("Title post %d", 1));
    post.setViewCount(2);
    post.setTags(tags);
    post.setPostVotes(postVotesList);
    post.setPostComments(postComments);
    post.setUser(getUser());
    return post;
  }

  private User getUser() {
    var user = new User();
    user.setEmail("b@b.user");
    user.setName("UserNameUser");
    user.setPassword("pass");
    user.setIsModerator((byte) 0);
    user.setRegTime(LocalDateTime.of(2010, 2, 18, 22, 22, 0));
    return user;
  }

  private User getUserModerator() {
    var userModerator = new User();
    userModerator.setEmail("a@a.moderator");
    userModerator.setName("NameModerator");
    userModerator.setPassword(" ");
    userModerator.setIsModerator((byte) 1);
    userModerator.setRegTime(LocalDateTime.of(2020, 1, 12, 12, 0));
    return userModerator;
  }
}
