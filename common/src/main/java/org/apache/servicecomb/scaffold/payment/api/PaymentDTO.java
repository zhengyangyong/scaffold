package org.apache.servicecomb.scaffold.payment.api;

public class PaymentDTO {
  private String transactionId;

  private String userName;

  private String bankName;

  private String cardNumber;

  private double amount;

  public String getTransactionId() {
    return transactionId;
  }

  public String getUserName() {
    return userName;
  }

  public String getBankName() {
    return bankName;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public double getAmount() {
    return amount;
  }

  public PaymentDTO() {
  }

  public PaymentDTO(String transactionId, String userName, String bankName, String cardNumber, double amount) {
    this.transactionId = transactionId;
    this.userName = userName;
    this.bankName = bankName;
    this.cardNumber = cardNumber;
    this.amount = amount;
  }
}
