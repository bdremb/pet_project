package com.diplom.skillbox.blog_driver.enums;

import static java.util.stream.Collectors.toList;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import java.util.Set;

public enum Role {
  USER(Set.of(Permission.USE)),
  MODERATOR(Set.of(Permission.USE, Permission.MODERATE));

  private final Set<Permission> permissions;

  Role(Set<Permission> permissions) {
    this.permissions = permissions;
  }

  public Set<Permission> getPermissions() {
    return permissions;
  }

  public List<SimpleGrantedAuthority> getAuthority() {
    return permissions.stream()
        .map(permission -> new SimpleGrantedAuthority(permission.getPermission())).collect(toList());
  }
}
