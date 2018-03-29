package org.apache.servicecomb.scaffold.user;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.scaffold.user.api.UserDTO;
import org.apache.servicecomb.scaffold.user.api.UserService;
import org.apache.servicecomb.swagger.invocation.exception.InvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
  public long logon(@RequestBody UserDTO user) {
    if (validateLogonUser(user)) {
      UserEntity dbUser = repository.findByName(user.getName());
      if (dbUser == null) {
        UserEntity entity = new UserEntity(user.getName(), user.getPassword(), user.getDeposit());
        repository.save(entity);
        return entity.getId();
      }
      throw new InvocationException(BAD_REQUEST, "user name had exist");
    }
    throw new InvocationException(BAD_REQUEST, "incorrect user");
  }

  @Override
  @PostMapping(path = "login")
  public long login(@RequestBody UserDTO user) {
    if (validateLoginUser(user)) {
      UserEntity dbUser = repository.findByName(user.getName());
      if (dbUser != null) {
        if (dbUser.getPassword().equals(user.getPassword())) {
          return dbUser.getId();
        }
        throw new InvocationException(BAD_REQUEST, "wrong password");
      }
      throw new InvocationException(BAD_REQUEST, "user name not exist");
    }
    throw new InvocationException(BAD_REQUEST, "incorrect user");
  }

  @Override
  @GetMapping(path = "getUserInfo")
  public UserDTO getUserInfo(String name) {
    UserEntity dbUser = repository.findByName(name);
    if (dbUser != null) {
      return new UserDTO(dbUser.getId(), dbUser.getName(), null, dbUser.getDeposit());
    }
    throw new InvocationException(BAD_REQUEST, "user name not exist");
  }

  private boolean validateLogonUser(UserDTO user) {
    return user != null && StringUtils.isNotEmpty(user.getName()) && StringUtils.isNotEmpty(user.getPassword())
        && user.getDeposit() > 0;
  }

  private boolean validateLoginUser(UserDTO user) {
    return user != null && StringUtils.isNotEmpty(user.getName()) && StringUtils.isNotEmpty(user.getPassword());
  }
}
