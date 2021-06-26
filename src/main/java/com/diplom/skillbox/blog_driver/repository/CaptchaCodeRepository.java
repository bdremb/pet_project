package com.diplom.skillbox.blog_driver.repository;

import com.diplom.skillbox.blog_driver.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Integer> {
  Optional<CaptchaCode> getCaptchaCodeBySecretCode(String secretCode);

  @Transactional
  void deleteAllByTimeIsBefore(LocalDateTime time);
}
