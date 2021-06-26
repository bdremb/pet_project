package com.diplom.skillbox.blog_driver.service;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.time.ZoneOffset.UTC;
import static java.util.Base64.getEncoder;
import static javax.imageio.ImageIO.write;
import static org.springframework.security.crypto.bcrypt.BCrypt.gensalt;
import static org.springframework.security.crypto.bcrypt.BCrypt.hashpw;

import com.diplom.skillbox.blog_driver.api.response.CaptchaResponse;
import com.diplom.skillbox.blog_driver.model.CaptchaCode;
import com.diplom.skillbox.blog_driver.repository.CaptchaCodeRepository;
import com.github.cage.Cage;
import com.github.cage.GCage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class CaptchaCodeServiceImpl implements CaptchaCodeService {
  final Logger logger = LoggerFactory.getLogger(CaptchaCodeServiceImpl.class);
  @Value("${captcha.hoursOfExist}")
  private int hours;
  private static final int WIDTH = 100;
  private static final int HEIGHT = 35;
  private static final int LOG_ROUNDS = 12;
  private static final int CAPTCHA_LENGTH_LIMITATION = 5;
  private static final String IMAGE_FORMAT_PNG = "png";
  private static final String LINE_HEADER = "data:image/png;base64,%s";
  private final CaptchaCodeRepository codeRepository;

  public CaptchaCodeServiceImpl(CaptchaCodeRepository codeRepository) {
    this.codeRepository = codeRepository;
  }

  @Override
  public CaptchaResponse captchaGenerator() {
    Cage cage = new GCage();
    String secretCode = cage.getTokenGenerator().next().substring(0, CAPTCHA_LENGTH_LIMITATION);
    String encodedCaptchaToString = getEncoder().encodeToString(getCaptchaImageContent(cage, secretCode));
    String encodedCaptchaSecretCode = hashpw(secretCode, gensalt(LOG_ROUNDS));
    var captchaCode = new CaptchaCode();
    captchaCode.setCode(secretCode);
    captchaCode.setSecretCode(encodedCaptchaSecretCode);
    captchaCode.setTime(now(UTC));
    codeRepository.deleteAllByTimeIsBefore(now(UTC).minusHours(hours));
    codeRepository.save(captchaCode);
    return new CaptchaResponse(encodedCaptchaSecretCode, format(LINE_HEADER, encodedCaptchaToString));
  }

  private byte[] getCaptchaImageContent(Cage cage, String secretCode) {
    var bufferedImage = new BufferedImage(WIDTH, HEIGHT, TYPE_INT_ARGB);
    var byteArrayOutputStream = new ByteArrayOutputStream();
    bufferedImage.createGraphics()
        .drawImage(cage.drawImage(secretCode), 0, 0, WIDTH, HEIGHT, null);
    try {
      write(bufferedImage, IMAGE_FORMAT_PNG, byteArrayOutputStream);
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
    return byteArrayOutputStream.toByteArray();
  }
}
