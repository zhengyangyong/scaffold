package org.apache.servicecomb.scaffold.payment;

import org.apache.servicecomb.springboot.starter.provider.EnableServiceComb;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableServiceComb
public class CanaryPaymentApplication {
  public static void main(String[] args) {
    SpringApplication.run(CanaryPaymentApplication.class, args);
  }
}