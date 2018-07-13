package org.apache.servicecomb.scaffold.edge;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.edge.core.AbstractEdgeDispatcher;
import org.apache.servicecomb.edge.core.EdgeInvocation;
import org.apache.servicecomb.scaffold.edge.darklaunch.DarkLaunchRule;
import org.apache.servicecomb.scaffold.edge.darklaunch.DynamicDarkLaunchRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.config.DynamicPropertyFactory;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CookieHandler;

public class EdgeDispatcher extends AbstractEdgeDispatcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(EdgeDispatcher.class);

  private static final ObjectMapper OBJ_MAPPER = new ObjectMapper();

  private final Map<String, DynamicDarkLaunchRule> darkLaunchRules = new ConcurrentHashMap<>();

  public EdgeDispatcher() {
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

    //检查灰度策略是否更新
    checkDarkLaunchRule(serviceName);

    //创建一个Edge转发
    EdgeInvocation edgeInvocation = new EdgeInvocation();
    //设定灰度版本策略
    edgeInvocation.setVersionRule(
        darkLaunchRules.get(serviceName).getRule().matchVersion(context.request().headers().entries()));
    edgeInvocation.init(serviceName, context, operationPath, httpServerFilters);
    edgeInvocation.edgeInvoke();
  }

  private void checkDarkLaunchRule(String serviceName) {
    final String config = DynamicPropertyFactory.getInstance()
        .getStringProperty("edge.dark-launch-rules." + serviceName, "").getValue();
    if (darkLaunchRules.containsKey(serviceName)) {
      DynamicDarkLaunchRule rule = darkLaunchRules.get(serviceName);
      if (!rule.getConfig().equals(config)) {
        darkLaunchRules.put(serviceName, new DynamicDarkLaunchRule(config, parseRule(config)));
      }
    } else {
      darkLaunchRules.computeIfAbsent(serviceName, s -> new DynamicDarkLaunchRule(config, parseRule(config)));
    }
  }

  private DarkLaunchRule parseRule(String config) {
    try {
      if (StringUtils.isNotEmpty(config)) {
        return OBJ_MAPPER.readValue(config, DarkLaunchRule.class);
      }
    } catch (IOException e) {
      LOGGER.error("parse rule failed", e);
    }
    return new DarkLaunchRule();
  }
}