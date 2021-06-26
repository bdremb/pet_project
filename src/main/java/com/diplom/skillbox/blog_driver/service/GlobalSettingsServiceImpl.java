package com.diplom.skillbox.blog_driver.service;

import static java.lang.String.valueOf;

import com.diplom.skillbox.blog_driver.model.GlobalSettings;
import com.diplom.skillbox.blog_driver.repository.GlobalSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class GlobalSettingsServiceImpl implements GlobalSettingsService {
  final Logger logger = LoggerFactory.getLogger(GlobalSettingsServiceImpl.class);
  private final GlobalSettingsRepository globalSettingsRepository;

  public GlobalSettingsServiceImpl(GlobalSettingsRepository globalSettingsRepository) {
    this.globalSettingsRepository = globalSettingsRepository;
  }

  @Override
  public Map<String, Boolean> getGlobalSettings() {
    return StreamSupport
        .stream(globalSettingsRepository.findAll().spliterator(), false)
        .collect(Collectors.toList())
        .stream().collect(Collectors.toMap(GlobalSettings::getCode,
            globalSettings -> globalSettings.getValue().equals(valueOf(true))));
  }

  @Override
  public void setGlobalSettings(Map<String, Boolean> requestSettings) {
    var settings = globalSettingsRepository.findAll();
    for (GlobalSettings set : settings) {
      set.setValue(requestSettings.get(set.getCode()).toString());
      logger.info("{} = {}", set.getCode(), set.getValue());
    }
    globalSettingsRepository.saveAll(settings);
  }
}