package com.diplom.skillbox.blog_driver.repository;

import com.diplom.skillbox.blog_driver.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {
  @Query(value = "SELECT DISTINCT t FROM Tag t "
      + " LEFT JOIN Tag2Post tp ON tp.tagId = t.id"
      + " LEFT JOIN Post p ON p.id = tp.postId"
      + " WHERE LOWER(t.name) LIKE LOWER(CONCAT('%',:name,'%'))"
      + " AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time <= :localDateTime")
  List<Tag> findTagsByName(String name, LocalDateTime localDateTime);

  @Query(value = "SELECT COUNT(tp) FROM Tag2Post tp"
      + " LEFT JOIN Tag t ON t.id = tp.tagId"
      + " LEFT JOIN Post p ON p.id = tp.postId"
      + " WHERE LOWER(t.name) LIKE LOWER(:name)"
      + " AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time <= :localDateTime")
  float countTagsByName(String name, LocalDateTime localDateTime);

  @Query(value = "SELECT COUNT(tp) FROM Tag2Post tp"
      + " LEFT JOIN Post p ON p.id = tp.postId"
      + " WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time <= :localDateTime")
  int countAllTags(LocalDateTime localDateTime);

  Optional<Tag> findTagsByName(String name);
}
