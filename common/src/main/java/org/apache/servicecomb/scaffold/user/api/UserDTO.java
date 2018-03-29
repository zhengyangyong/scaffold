package org.apache.servicecomb.scaffold.user.api;

public class UserDTO {
  private long id;

  private String name;

  private String password;

  private Double deposit;

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }

  public Double getDeposit() {
    return deposit;
  }

  public UserDTO() {
  }

  public UserDTO(long id, String name, String password, Double deposit) {
    this.id = id;
    this.name = name;
    this.password = password;
    this.deposit = deposit;
  }
}
