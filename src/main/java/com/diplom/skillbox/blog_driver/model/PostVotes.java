package com.diplom.skillbox.blog_driver.model;

import static javax.persistence.GenerationType.IDENTITY;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_votes")
public class PostVotes {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private int id;

  @Column(name = "time", columnDefinition = "DATETIME", nullable = false)
  private LocalDateTime time;

  @Column(name = "value", nullable = false)
  private byte value;

  @Column(name = "post_id", nullable = false)
  private int postId;

  @ManyToOne
  @JoinColumn(name = "user_id",
      referencedColumnName = "id")
  private User user;
}
