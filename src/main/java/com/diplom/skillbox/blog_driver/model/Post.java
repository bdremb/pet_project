package com.diplom.skillbox.blog_driver.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import com.diplom.skillbox.blog_driver.enums.ModerationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "posts")
public class Post {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private int id;

  @Column(name = "is_active", nullable = false)
  private byte isActive;

  @Column(name = "moderation_status",
      columnDefinition = "enum('NEW','ACCEPTED','DECLINED')", nullable = false)
  @Enumerated(value = STRING)
  private ModerationStatus moderationStatus;

  @Column(name = "time", nullable = false)
  private LocalDateTime time;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "text", columnDefinition = "TEXT", nullable = false)
  private String text;

  @Column(name = "view_count", nullable = false)
  private int viewCount;

  @OneToMany
  @JoinColumn(name = "post_id",
      referencedColumnName = "id",
      insertable = false, updatable = false)
  private List<PostVotes> postVotes;

  @OneToMany(mappedBy = "postId")
  private List<PostComment> postComments;

  @ManyToMany(cascade = ALL)
  @JoinTable(name = "tag2post",
      joinColumns = {@JoinColumn(name = "post_id")},
      inverseJoinColumns = {@JoinColumn(name = "tag_id")}
  )
  private List<Tag> tags;

  @ManyToOne(cascade = ALL)
  @JoinColumn(name = "user_id",
      referencedColumnName = "id")
  private User user;

  @ManyToOne(cascade = ALL)
  @JoinColumn(name = "moderator_id",
      referencedColumnName = "id")
  private User moderator;
}
