package org.apache.servicecomb.scaffold.edge.darklaunch;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class DarkLaunchRuleItem {

  private static Map<DarkLaunchOperator, BiFunction<String, String, Boolean>> matchFuncs = new HashMap<>();

  static {
    matchFuncs.put(DarkLaunchOperator.CONTAINS, String::contains);
    matchFuncs.put(DarkLaunchOperator.START_WITH, String::startsWith);
    matchFuncs.put(DarkLaunchOperator.END_WITH, String::endsWith);
    matchFuncs.put(DarkLaunchOperator.EQUALS, String::equals);
  }

  private DarkLaunchOperator operator;

  private String value;

  private String version;

  public DarkLaunchOperator getOperator() {
    return operator;
  }

  public void setOperator(DarkLaunchOperator operator) {
    this.operator = operator;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public DarkLaunchRuleItem() {
  }

  public DarkLaunchRuleItem(DarkLaunchOperator operator, String value, String version) {
    this.operator = operator;
    this.value = value;
    this.version = version;
  }

  public boolean match(String value) {
    return matchFuncs.get(operator).apply(value, this.value);
  }
}