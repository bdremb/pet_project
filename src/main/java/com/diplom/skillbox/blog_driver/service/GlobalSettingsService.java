package com.diplom.skillbox.blog_driver.service;

import java.util.Map;

public interface GlobalSettingsService {
  Map<String, Boolean> getGlobalSettings();

  void setGlobalSettings(Map<String, Boolean> requestSettings);
}
