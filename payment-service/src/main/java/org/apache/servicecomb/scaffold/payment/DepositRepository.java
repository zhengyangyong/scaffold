package org.apache.servicecomb.scaffold.payment;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Repository
@EnableTransactionManagement
public interface DepositRepository extends PagingAndSortingRepository<DepositEntity, String> {
  DepositEntity findByUserName(String userName);
}
