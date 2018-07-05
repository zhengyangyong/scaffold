package org.apache.servicecomb.scaffold.payment;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Repository
@EnableTransactionManagement
public interface PaymentRepository extends PagingAndSortingRepository<PaymentEntity, Long> {
  List<PaymentEntity> findByUserName(String userName);

  PaymentEntity findByTransactionId(String TransactionId);
}
