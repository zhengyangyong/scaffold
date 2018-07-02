package org.apache.servicecomb.scaffold.user;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.scaffold.user.api.AuthenticationService;
import org.apache.servicecomb.swagger.invocation.exception.InvocationException;
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
  public String validate(String token) {
    String userName = tokenStore.validate(token);
    if (userName == null) {
      throw new InvocationException(BAD_REQUEST, "incorrect token");
    }
    return userName;
  }
}