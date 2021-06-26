package com.diplom.skillbox.blog_driver.model;

import static javax.persistence.GenerationType.IDENTITY;

import com.diplom.skillbox.blog_driver.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private int id;

  @Column(name = "is_moderator", nullable = false)
  private byte isModerator;

  @Column(name = "reg_time", columnDefinition = "DATETIME", nullable = false)
  private LocalDateTime regTime;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "code")
  private String code;

  @Column(name = "photo", columnDefinition = "TEXT")
  private String photo;

  public Role getRole() {
    return isModerator == 1 ? Role.MODERATOR : Role.USER;
  }

  public boolean getIsModerator() {
    return isModerator == 1;
  }
}
