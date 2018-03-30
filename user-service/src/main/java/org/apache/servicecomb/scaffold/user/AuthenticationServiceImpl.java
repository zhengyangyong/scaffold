package org.apache.servicecomb.scaffold.user;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.scaffold.user.api.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestSchema(schemaId = "authentication")
@RequestMapping(path = "/")
public class AuthenticationServiceImpl implements AuthenticationService {

  private final TokenStore tokenStore;

  @Autowired
  public AuthenticationServiceImpl(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
  }

  @Override
  @GetMapping(path = "validate")
  public boolean validate(String token) {
    return tokenStore.validate(token);
  }
}