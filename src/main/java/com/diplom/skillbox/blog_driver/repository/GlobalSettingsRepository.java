package com.diplom.skillbox.blog_driver.repository;

import com.diplom.skillbox.blog_driver.model.GlobalSettings;
import org.springframework.data.repository.CrudRepository;

public interface GlobalSettingsRepository extends CrudRepository<GlobalSettings, Integer> {
  GlobalSettings getByCode(String code);
}
