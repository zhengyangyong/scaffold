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
import org.apache.servicecomb.common.rest.filter.HttpServerFilter;
import org.apache.servicecomb.core.Invocation;
import org.apache.servicecomb.foundation.vertx.http.HttpServletRequestEx;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.apache.servicecomb.swagger.invocation.Response;
import org.apache.servicecomb.swagger.invocation.exception.InvocationException;
import org.springframework.web.client.RestTemplate;

public class AuthenticationFilter implements HttpServerFilter {

  private final RestTemplate template = RestTemplateBuilder.create();

  private static final String USER_SERVICE_NAME = "user-service";

  public static final String EDGE_AUTHENTICATION_NAME = "edge-authentication-name";

  private static final Set<String> NOT_REQUIRED_VERIFICATION_USER_SERVICE_METHODS = new HashSet<>(
      Arrays.asList("login", "logon", "validate"));

  @Override
  public int getOrder() {
    return 0;
  }

  @Override
  public Response afterReceiveRequest(Invocation invocation, HttpServletRequestEx httpServletRequestEx) {
    if (isInvocationNeedValidate(invocation.getMicroserviceName(), invocation.getOperationName())) {
      String token = httpServletRequestEx.getHeader(AUTHORIZATION);
      if (StringUtils.isNotEmpty(token)) {
        String userName = template
            .getForObject("cse://" + USER_SERVICE_NAME + "/validate?token={token}", String.class, token);
        if (StringUtils.isNotEmpty(userName)) {
          //Add header
          invocation.getContext().put(EDGE_AUTHENTICATION_NAME, userName);
        } else {
          return Response
              .failResp(new InvocationException(Status.UNAUTHORIZED, "authentication failed, invalid token"));
        }
      } else {
        return Response.failResp(
            new InvocationException(Status.UNAUTHORIZED, "authentication failed, missing AUTHORIZATION header"));
      }
    }
    return null;
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
