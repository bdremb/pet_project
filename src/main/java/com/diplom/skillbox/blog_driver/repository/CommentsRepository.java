package com.diplom.skillbox.blog_driver.repository;

import com.diplom.skillbox.blog_driver.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CommentsRepository extends JpaRepository<PostComment, Integer> {
  @Query(value = "SELECT pc FROM PostComment pc WHERE pc.parentId = :parentId")
  List<PostComment> findPostCommentsByParentId(int parentId);

  @Query(value = "SELECT MAX(pc.id) FROM PostComment pc")
  int findIndexOfLastAddedComment();
}
