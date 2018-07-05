package org.apache.servicecomb.scaffold.edge;

import org.apache.servicecomb.swagger.invocation.exception.InvocationException;

import io.vertx.ext.web.RoutingContext;

public interface EdgeFilter {
  int getOrder();

  //如果需要中止Filter链执行，抛InvocationException即可
  void processing(String serviceName, String operationPath, RoutingContext context) throws InvocationException;
}
