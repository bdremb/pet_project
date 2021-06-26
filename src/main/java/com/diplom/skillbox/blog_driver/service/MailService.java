package com.diplom.skillbox.blog_driver.service;

public interface MailService {

  void sendEmail(String emailTo, String subject, String message) throws Exception;
}
