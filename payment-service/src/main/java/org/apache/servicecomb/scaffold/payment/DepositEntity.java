package org.apache.servicecomb.scaffold.payment;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "T_Deposit")
public class DepositEntity {
  @Id
  private String userName;

  private double amount;

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public DepositEntity() {
  }

  public DepositEntity(String userName, double amount) {
    this.userName = userName;
    this.amount = amount;
  }
}
