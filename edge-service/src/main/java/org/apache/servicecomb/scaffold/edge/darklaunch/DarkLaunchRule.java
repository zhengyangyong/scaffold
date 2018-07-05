package org.apache.servicecomb.scaffold.edge.darklaunch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.servicecomb.serviceregistry.definition.DefinitionConst;

public class DarkLaunchRule {

  private Map<String, List<DarkLaunchRuleItem>> headerRules;

  private String defaultVersion;

  public Map<String, List<DarkLaunchRuleItem>> getHeaderRules() {
    return headerRules;
  }

  public void setHeaderRules(
      Map<String, List<DarkLaunchRuleItem>> headerRules) {
    this.headerRules = headerRules;
  }

  public String getDefaultVersion() {
    return defaultVersion;
  }

  public void setDefaultVersion(String defaultVersion) {
    this.defaultVersion = defaultVersion;
  }

  public DarkLaunchRule() {
    headerRules = new HashMap<>();
    defaultVersion = DefinitionConst.VERSION_RULE_ALL;
  }

  public DarkLaunchRule(List<DarkLaunchRuleItem> userRules,
      Map<String, List<DarkLaunchRuleItem>> headerRules, String defaultVersion) {
    this.headerRules = headerRules;
    this.defaultVersion = defaultVersion;
  }

  public String matchVersion(Map<String, String> headers) {
    for (Entry<String, List<DarkLaunchRuleItem>> rule : headerRules.entrySet()) {
      if (headers.containsKey(rule.getKey())) {
        String value = headers.get(rule.getKey());
        for (DarkLaunchRuleItem item : rule.getValue()) {
          if (item.match(value)) {
            return item.getVersion();
          }
        }
      }
    }
    return defaultVersion;
  }

  public String matchVersion(List<Entry<String, String>> headers) {
    for (Entry<String, String> header : headers) {
      if (headerRules.containsKey(header.getKey())) {
        for (DarkLaunchRuleItem item : headerRules.get(header.getKey())) {
          if (item.match(header.getValue())) {
            return item.getVersion();
          }
        }
      }
    }
    return defaultVersion;
  }
}