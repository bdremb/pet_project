package com.diplom.skillbox.blog_driver.api.response;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class InitResponse {
  @Value("${init.title}")
  private String title;
  @Value("${init.subtitle}")
  private String subtitle;
  @Value("${init.phone}")
  private String phone;
  @Value("${init.email}")
  private String email;
  @Value("${init.copyright}")
  private String copyright;
  @Value("${init.copyrightFrom}")
  private String copyrightFrom;
}
