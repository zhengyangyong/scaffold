/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.scaffold.edge.filter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.apache.servicecomb.scaffold.edge.EdgeFilter;
import org.apache.servicecomb.swagger.invocation.exception.InvocationException;
import org.springframework.web.client.RestTemplate;

import io.vertx.ext.web.RoutingContext;

public class AuthenticationFilter implements EdgeFilter {

  private final RestTemplate template = RestTemplateBuilder.create();

  private static final String USER_SERVICE_NAME = "user-service";

  public static final String EDGE_AUTHENTICATION_NAME = "edge-authentication-name";

  private static final Set<String> NOT_REQUIRED_VERIFICATION_USER_SERVICE_METHODS = new HashSet<>(
      Arrays.asList("/login", "/logon", "/validate"));

  @Override
  public int getOrder() {
    return 0;
  }

  @Override
  public void processing(String serviceName, String operationPath, RoutingContext context) throws InvocationException {
    if (isInvocationNeedValidate(serviceName, operationPath)) {
      String token = context.request().headers().get(AUTHORIZATION);
      if (StringUtils.isNotEmpty(token)) {
        String userName = template
            .getForObject("cse://" + USER_SERVICE_NAME + "/validate?token={token}", String.class, token);
        if (StringUtils.isNotEmpty(userName)) {
          //Add header
          context.request().headers().add(EDGE_AUTHENTICATION_NAME, userName);
        } else {
          throw new InvocationException(Status.UNAUTHORIZED, "authentication failed, invalid token");
        }
      } else {
        throw new InvocationException(Status.UNAUTHORIZED, "authentication failed, missing AUTHORIZATION header");
      }
    }
  }

  private boolean isInvocationNeedValidate(String serviceName, String operationPath) {
    if (USER_SERVICE_NAME.equals(serviceName)) {
      for (String method : NOT_REQUIRED_VERIFICATION_USER_SERVICE_METHODS) {
        if (operationPath.startsWith(method)) {
          return false;
        }
      }
    }
    return true;
  }
}
