package org.apache.servicecomb.scaffold.log.api;

import java.util.Date;

public class LogDTO {
  private String userName;

  private String serviceName;

  private String operationName;

  private Date invokeTime;

  public String getUserName() {
    return userName;
  }

  public String getServiceName() {
    return serviceName;
  }

  public String getOperationName() {
    return operationName;
  }

  public Date getInvokeTime() {
    return invokeTime;
  }

  public LogDTO() {
  }

  public LogDTO(String userName, String serviceName, String operationName, Date invokeTime) {
    this.userName = userName;
    this.serviceName = serviceName;
    this.operationName = operationName;
    this.invokeTime = invokeTime;
  }
}
