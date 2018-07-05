package org.apache.servicecomb.scaffold.edge.darklaunch;

//保存灰度发布动态配置，避免JSON反序列化开销
public class DynamicDarkLaunchRule {
  private final String config;

  private final DarkLaunchRule rule;

  public String getConfig() {
    return config;
  }

  public DarkLaunchRule getRule() {
    return rule;
  }

  public DynamicDarkLaunchRule(String config, DarkLaunchRule rule) {
    this.config = config;
    this.rule = rule;
  }
}