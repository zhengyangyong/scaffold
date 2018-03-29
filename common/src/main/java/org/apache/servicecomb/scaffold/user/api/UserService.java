package org.apache.servicecomb.scaffold.user.api;

public interface UserService {

  long logon(UserDTO user);

  long login(UserDTO user);

  UserDTO getUserInfo(String name);
}
