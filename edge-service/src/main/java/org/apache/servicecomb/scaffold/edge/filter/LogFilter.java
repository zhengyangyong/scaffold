package org.apache.servicecomb.scaffold.edge.filter;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.springmvc.reference.async.CseAsyncRestTemplate;
import org.apache.servicecomb.scaffold.edge.EdgeFilter;
import org.apache.servicecomb.scaffold.log.api.LogDTO;
import org.apache.servicecomb.swagger.invocation.exception.InvocationException;
import org.springframework.http.HttpEntity;

import io.vertx.ext.web.RoutingContext;

public class LogFilter implements EdgeFilter {

  private static final String LOG_SERVICE_NAME = "infrastructure:log-service";

  private final CseAsyncRestTemplate restTemplate = new CseAsyncRestTemplate();

  @Override
  public int getOrder() {
    return 1;
  }

  @Override
  public void processing(String serviceName, String operationName, RoutingContext context)
      throws InvocationException {
    //Log记录失败应不影响业务逻辑，异步请求无需等待
    String userName = context.request().headers().get(AuthenticationFilter.EDGE_AUTHENTICATION_NAME);
    if (StringUtils.isNotEmpty(userName)) {
      HttpEntity<LogDTO> request = new HttpEntity<>(
          new LogDTO(userName, serviceName, operationName, new Date()));
      try {
        restTemplate.postForEntity("cse://" + LOG_SERVICE_NAME + "/record", request, Boolean.class);
      } catch (Exception ignored) {
      }
    }
  }
}
