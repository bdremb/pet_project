package com.diplom.skillbox.blog_driver.enums;

public enum ErrorReason {
  ERROR_TITLE("title"),
  ERROR_TEXT("text"),
  ERROR_EXCEPTION("exception"),
  ERROR_IMAGE("image"),
  ERROR_NAME("name"),
  ERROR_EMAIL("email"),
  ERROR_PASSWORD("password"),
  ERROR_CAPTCHA("captcha"),
  ERROR_CODE("code");

  private final String reason;

  ErrorReason(java.lang.String reason) {
    this.reason = reason;
  }

  public java.lang.String get() {
    return reason;
  }
}
