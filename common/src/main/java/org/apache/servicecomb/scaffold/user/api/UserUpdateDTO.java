package org.apache.servicecomb.scaffold.user.api;

public class UserUpdateDTO {
  private String name;

  private String oldPassword;

  private String newPassword;

  public String getName() {
    return name;
  }

  public String getOldPassword() {
    return oldPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public UserUpdateDTO() {
  }

  public UserUpdateDTO(String name, String oldPassword, String newPassword) {
    this.name = name;
    this.oldPassword = oldPassword;
    this.newPassword = newPassword;
  }
}
