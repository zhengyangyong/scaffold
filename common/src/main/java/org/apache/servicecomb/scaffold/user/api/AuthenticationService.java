package org.apache.servicecomb.scaffold.user.api;

public interface AuthenticationService {
  String validate(String token);
}