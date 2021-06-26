package com.diplom.skillbox.blog_driver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class ConfigEmail {
  @Value("${spring.mail.host}")
  private String host;
  @Value("${spring.mail.username}")
  private String username;
  @Value("${spring.mail.password}")
  private String password;
  @Value("${spring.mail.port}")
  private int port;
  @Value("${spring.mail.protocol}")
  private String protocol;
  @Value("${spring.mail.debug}")
  private String debug;

  @Bean
  public JavaMailSender getMailSender() {
    var mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);
    mailSender.setUsername(username);
    mailSender.setPassword(password);
    var properties = mailSender.getJavaMailProperties();
    properties.setProperty("mail.transport.protocol", protocol);
    properties.setProperty("mail.debug", debug);
    return mailSender;
  }
}
