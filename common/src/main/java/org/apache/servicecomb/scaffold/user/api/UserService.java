package org.apache.servicecomb.scaffold.user.api;

import org.springframework.http.ResponseEntity;

public interface UserService {
  ResponseEntity<Boolean> logon(UserDTO user);

  ResponseEntity<Boolean> login(UserDTO user);
}
