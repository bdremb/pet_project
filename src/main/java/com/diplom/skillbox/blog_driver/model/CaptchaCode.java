package com.diplom.skillbox.blog_driver.model;

import static javax.persistence.GenerationType.IDENTITY;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "captcha_codes")
public class CaptchaCode {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private int id;

  @Column(name = "time", nullable = false)
  private LocalDateTime time;

  @Column(name = "code", nullable = false)
  private String code;

  @Column(name = "secret_code", nullable = false)
  private String secretCode;
}
