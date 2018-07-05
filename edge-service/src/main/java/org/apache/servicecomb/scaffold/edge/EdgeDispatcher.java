package org.apache.servicecomb.scaffold.edge;

import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.core.Response.Status;

import org.apache.servicecomb.edge.core.AbstractEdgeDispatcher;
import org.apache.servicecomb.edge.core.EdgeInvocation;
import org.apache.servicecomb.foundation.common.utils.SPIServiceUtils;
import org.apache.servicecomb.serviceregistry.definition.DefinitionConst;
import org.apache.servicecomb.swagger.invocation.exception.InvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.config.DynamicPropertyFactory;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CookieHandler;

public class EdgeDispatcher extends AbstractEdgeDispatcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(EdgeDispatcher.class);

  private final List<EdgeFilter> filterChain;

  public EdgeDispatcher() {
    filterChain = SPIServiceUtils.getSortedService(EdgeFilter.class);
  }

  //此Dispatcher的优先级，Order级越小，路由策略优先级越高
  public int getOrder() {
    return 10000;
  }

  //初始化Dispatcher的路由策略
  public void init(Router router) {
    ///捕获 {ServiceComb微服务Name}/{服务路径&参数} 的URL
    String regex = "/([^\\\\/]+)/(.*)";
    router.routeWithRegex(regex).handler(CookieHandler.create());
    router.routeWithRegex(regex).handler(createBodyHandler());
    router.routeWithRegex(regex).failureHandler(this::onFailure).handler(this::onRequest);
  }

  //处理请求，请注意
  private void onRequest(RoutingContext context) {
    Map<String, String> pathParams = context.pathParams();
    //从匹配的param0拿到{ServiceComb微服务Name}
    final String service = pathParams.get("param0");
    //从匹配的param1拿到{服务路径&参数}
    String operationPath = "/" + pathParams.get("param1");

    //还记得我们之前说的做出一点点改进吗？引入一个自定义配置edge.routing-short-path.{简称}，映射微服务名；如果简称没有配置，那么就认为直接是微服务的名
    final String serviceName = DynamicPropertyFactory.getInstance()
        .getStringProperty("edge.routing-short-path." + service, service).get();

    //创建一个Edge转发
    EdgeInvocation edgeInvocation = new EdgeInvocation();
    //允许接受任意版本的微服务实例作为Provider，未来我们会使用此（设置版本）能力实现灰度发布
    edgeInvocation.setVersionRule(DefinitionConst.VERSION_RULE_ALL);
    edgeInvocation.init(serviceName, context, operationPath, httpServerFilters);

    //处理Filter链并转发请求
    loopExecuteEdgeFilterInChain(0, serviceName, operationPath, context, edgeInvocation);
  }

  private void loopExecuteEdgeFilterInChain(int index, String serviceName, String operationPath, RoutingContext context,
      EdgeInvocation edgeInvocation) {
    if (index < filterChain.size()) {
      EdgeFilter filter = filterChain.get(index);
      AtomicReference<InvocationException> exception = new AtomicReference<>();
      CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        try {
          filter.processing(serviceName, operationPath, context);
        } catch (InvocationException e) {
          exception.set(e);
        }
      });

      future.whenComplete((result, throwable) -> {
        if (exception.get() != null) {
          sendFailed(context, exception.get());
        } else if (throwable != null) {
          sendFailed(context, new InvocationException(Status.INTERNAL_SERVER_ERROR, throwable.getMessage()));
        } else {
          loopExecuteEdgeFilterInChain(index + 1, serviceName, operationPath, context, edgeInvocation);
        }
      });
    } else {
      try {
        edgeInvocation.edgeInvoke();
      } catch (InvocationException e) {
        sendFailed(context, e);
      }
    }
  }

  private void sendFailed(RoutingContext context, InvocationException exception) {
    context.response().setStatusCode(exception.getStatusCode());
    context.response().headers().add(CONTENT_LENGTH, String.valueOf(exception.getMessage().length()));
    context.response().write(exception.getMessage());
    context.response().end();
  }
}