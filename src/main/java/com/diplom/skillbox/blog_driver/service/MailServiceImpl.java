package com.diplom.skillbox.blog_driver.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
  @Value("${spring.mail.username}")
  private String username;
  private final JavaMailSender sender;

  public MailServiceImpl(@Qualifier("getMailSender") JavaMailSender sender) {
    this.sender = sender;
  }

  @Override
  public void sendEmail(String emailTo, String subject, String message) throws Exception {
    var mailMessage = new SimpleMailMessage();
    mailMessage.setFrom(username);
    mailMessage.setTo(emailTo);
    mailMessage.setSubject(subject);
    mailMessage.setText(message);
    sender.send(mailMessage);
  }
}
