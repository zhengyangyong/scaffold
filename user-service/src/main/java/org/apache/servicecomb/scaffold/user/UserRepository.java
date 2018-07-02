package org.apache.servicecomb.scaffold.user;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, String> {
  UserEntity findByName(String name);
}
