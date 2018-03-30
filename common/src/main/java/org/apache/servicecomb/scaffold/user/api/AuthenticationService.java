package org.apache.servicecomb.scaffold.user.api;

public interface AuthenticationService {
  boolean validate(String token);
}