package com.diplom.skillbox.blog_driver.enums;

public enum Permission {
  USE("user:read"),
  MODERATE("user:moderate");

  private final String permission;

  Permission(String permission) {
    this.permission = permission;
  }

  public String getPermission() {
    return permission;
  }
}
