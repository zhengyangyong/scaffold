package org.apache.servicecomb.scaffold.user.api;

public class UserDTO {
  private String name;

  private String password;

  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }

  public UserDTO() {
  }

  public UserDTO(String name, String password) {
    this.name = name;
    this.password = password;
  }
}
