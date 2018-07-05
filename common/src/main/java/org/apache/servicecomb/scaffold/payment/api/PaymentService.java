package org.apache.servicecomb.scaffold.payment.api;

import org.springframework.http.ResponseEntity;

public interface PaymentService {
  ResponseEntity<Boolean> deposit(PaymentDTO payment);
}
