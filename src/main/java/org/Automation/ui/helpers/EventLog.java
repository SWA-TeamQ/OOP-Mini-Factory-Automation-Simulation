package org.Automation.ui.helpers;

import java.util.*;

class EventLogView {
  private Queue<String> events = new LinkedList<>();
  private int maxSize = 10;

  EventLogView(int maxSize) {
    this.maxSize = maxSize;
  }

  public void add(String event) {
    if (events.size() == maxSize) {
      events.poll();
    }
    events.add(event);
  }

  public void display() {
    events.forEach(event -> System.out.println(event));
  }
}
