package org.apache.servicecomb.scaffold.user;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.scaffold.user.api.UserDTO;
import org.apache.servicecomb.scaffold.user.api.UserService;
import org.apache.servicecomb.swagger.invocation.exception.InvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestSchema(schemaId = "user")
@RequestMapping(path = "/")
public class UserServiceImpl implements UserService {
  private final UserRepository repository;

  @Autowired
  public UserServiceImpl(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  @PostMapping(path = "logon")
  public ResponseEntity<Boolean> logon(@RequestBody UserDTO user) {
    if (validateUser(user)) {
      UserEntity dbUser = repository.findByName(user.getName());
      if (dbUser == null) {
        UserEntity entity = new UserEntity(user.getName(), user.getPassword());
        repository.save(entity);
        return new ResponseEntity<>(true, HttpStatus.OK);
      }
      throw new InvocationException(BAD_REQUEST, "user name had exist");
    }
    throw new InvocationException(BAD_REQUEST, "incorrect user");
  }

  @Override
  @PostMapping(path = "login")
  public ResponseEntity<Boolean> login(@RequestBody UserDTO user) {
    if (validateUser(user)) {
      UserEntity dbUser = repository.findByName(user.getName());
      if (dbUser != null) {
        if (dbUser.getPassword().equals(user.getPassword())) {
          return new ResponseEntity<>(true, HttpStatus.OK);
        }
        throw new InvocationException(BAD_REQUEST, "wrong password");
      }
      throw new InvocationException(BAD_REQUEST, "user name not exist");
    }
    throw new InvocationException(BAD_REQUEST, "incorrect user");
  }

  private boolean validateUser(UserDTO user) {
    return user != null && StringUtils.isNotEmpty(user.getName()) && StringUtils.isNotEmpty(user.getPassword());
  }
}
