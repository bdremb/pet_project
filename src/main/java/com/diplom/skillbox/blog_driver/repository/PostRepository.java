package com.diplom.skillbox.blog_driver.repository;

import com.diplom.skillbox.blog_driver.enums.ModerationStatus;
import com.diplom.skillbox.blog_driver.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
  List<Post> findPostsByUserEmailAndIsActiveAndModerationStatusOrderByTime(String userEmail,
                                                                           byte isActive,
                                                                           ModerationStatus moderationStatus);

  @Query(value = "SELECT p FROM Post p ORDER BY p.time")
  List<Post> findAllPostsOrderByTime();

  @Query(value = "SELECT posts.*,"
      + " ((SELECT COUNT(id) FROM post_votes WHERE post_votes.post_id = posts.id AND value = 1) - "
      + " (SELECT COUNT(id) FROM post_votes WHERE post_votes.post_id = posts.id AND value = -1)) AS likeCount,"
      + " (SELECT COUNT(id) FROM post_comments WHERE post_comments.post_id = posts.id) AS commentCount"
      + " FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time <= ?1",
      countQuery = "SELECT COUNT(*) FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time <= ?1",
      nativeQuery = true)
  Page<Post> findAllPosts(LocalDateTime localDateTime, Pageable pageable);

  @Query(value = "SELECT p FROM Post p"
      + " WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED'"
      + " AND YEAR(p.time) = YEAR(:localDate) AND MONTH(p.time) = MONTH(:localDate) AND DAY(p.time) = DAY(:localDate)")
  Page<Post> findPostsByDate(LocalDate localDate, Pageable pageable);

//  @Query(value = "SELECT p FROM Post p"
//      + " WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND YEAR(p.time) = YEAR(:localDate)")
//  List<Post> findAllPostsByYear(LocalDate localDate);

  @Query(value = "SELECT p FROM Post p"
      + " LEFT JOIN FETCH Tag2Post tp ON tp.postId = p.id"
      + " LEFT JOIN FETCH Tag t ON t.id = tp.tagId"
      + " WHERE t.name LIKE CONCAT(:tagName,'%')"
      + " AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time <= :localDateTime")
  Page<Post> findPostsByTags(String tagName, LocalDateTime localDateTime, Pageable pageable);

  @Query(value = "SELECT p FROM Post p"
      + " WHERE LOWER(p.title) LIKE LOWER(CONCAT('%',:query,'%'))"
      + " AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time <= :localDateTime")
  Page<Post> findPostsByQuery(String query, LocalDateTime localDateTime, Pageable pageable);

  @Query(value = "SELECT p FROM Post p WHERE p.id = :id "
      + " AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time <= :localDateTime")
  Optional<Post> findAcceptedPostById(int id, LocalDateTime localDateTime);

  Optional<Post> findPostById(int id);

  @Query(value = "SELECT p FROM Post p "
      + " WHERE p.isActive = 1 AND p.moderationStatus = :status AND p.moderator.email = :userEmail")
  Page<Post> findActivePostsForModerate(String userEmail, ModerationStatus status, Pageable pageable);

  @Query(value = "SELECT p FROM Post p "
      + " WHERE p.isActive = 1 AND p.moderationStatus = :status AND p.moderator IS NULL")
  Page<Post> findNewActivePostsForModerate(ModerationStatus status, Pageable pageable);

  @Query(value = "SELECT p FROM Post p "
      + " WHERE p.user.email = :userEmail AND p.isActive = 1 AND p.moderationStatus = :status")
  Page<Post> findUserActivePosts(String userEmail, ModerationStatus status, Pageable pageable);

  @Query(value = "SELECT p FROM Post p "
      + " WHERE p.user.email = :userEmail AND p.isActive = 0")
  Page<Post> findUserInactivePosts(String userEmail, Pageable pageable);

  @Query(value = "SELECT DISTINCT YEAR(p.time) AS years FROM Post p "
      + " WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time <= :localDateTime"
      + " ORDER BY years")
  List<Integer> findYearsOfPosts(LocalDateTime localDateTime);

//  @Query(value = "SELECT DATE_FORMAT(p.time,'%Y-%m-%d' ) FROM Post p WHERE YEAR(p.time) = :year "
//      + " AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED'")
//  List<String> findDatesOfPostsByYear(int year);

  @Query(value = "SELECT COUNT(p) FROM Post p WHERE p.moderationStatus = 'NEW' AND p.moderator IS NULL ")
  int findCountOfPostForModeration(String userEmail);
}
