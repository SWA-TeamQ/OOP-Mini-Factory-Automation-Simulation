package org.automations.ui.helpers;

import java.util.*;

public class StatusView {
  private Map<String, String> values = new LinkedHashMap<>();

  public void set(String key, Object value) {
    values.put(key, String.valueOf(value));
  }

  public void display() {
    values.forEach((key, value) -> System.out.println(String.format("%-20s : %s", key, value)));
  }
}
