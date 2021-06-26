package com.diplom.skillbox.blog_driver.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigCloudinary {
  @Value("${cloudinary.cloud-name}")
  public String cloudName;
  @Value("${cloudinary.api-key}")
  public String apiKey;
  @Value("${cloudinary.api-secret}")
  public String apiSecret;

  @Bean
  public Cloudinary getCloudinary() {
    return new Cloudinary(ObjectUtils.asMap(
        "cloud_name", cloudName,
        "api_key", apiKey,
        "api_secret", apiSecret));
  }
}
