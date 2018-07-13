package org.apache.servicecomb.scaffold.edge.filter;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.common.rest.filter.HttpServerFilter;
import org.apache.servicecomb.core.Invocation;
import org.apache.servicecomb.foundation.vertx.http.HttpServletRequestEx;
import org.apache.servicecomb.provider.springmvc.reference.async.CseAsyncRestTemplate;
import org.apache.servicecomb.scaffold.log.api.LogDTO;
import org.apache.servicecomb.swagger.invocation.Response;
import org.springframework.http.HttpEntity;

public class LogFilter implements HttpServerFilter {

  private static final String LOG_SERVICE_NAME = "infrastructure:log-service";

  private final CseAsyncRestTemplate restTemplate = new CseAsyncRestTemplate();

  @Override
  public int getOrder() {
    return 1;
  }

  @Override
  public Response afterReceiveRequest(Invocation invocation, HttpServletRequestEx httpServletRequestEx) {
    String userName = invocation.getContext().get(AuthenticationFilter.EDGE_AUTHENTICATION_NAME);
    if (StringUtils.isNotEmpty(userName)) {
      HttpEntity<LogDTO> request = new HttpEntity<>(
          new LogDTO(userName, invocation.getMicroserviceName(), invocation.getOperationName(), new Date()));
      try {
        restTemplate.postForEntity("cse://" + LOG_SERVICE_NAME + "/record", request, Boolean.class);
      } catch (Exception ignored) {
      }
    }
    return null;
  }
}
