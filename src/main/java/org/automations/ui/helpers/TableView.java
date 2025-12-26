package org.automations.ui.helpers;

import java.util.*;

public class TableView {
  protected List<String> headers = new ArrayList<String>();
  protected List<List<String>> rows = new ArrayList<>();

  public void setHeader(String... headerItems) {
    headers.clear();
    headers.addAll(Arrays.asList(headerItems));
  }

  public void addRow(Object... rowItems) throws IllegalArgumentException {
    if (rowItems.length != headers.size()) {
      throw new IllegalArgumentException("Row length doesn't match header length");
    }
    List<String> row = new ArrayList<String>();
    for (Object item : rowItems)
      row.add(String.valueOf(item));

    rows.add(row);
  }

  public void clear() {
    headers.clear();
    rows.clear();
  }

  public void display() {
    for (String h : headers)
      System.out.printf("%-15s", h);

    System.out.println();

    System.out.println("-".repeat(headers.size() * 15));

    for (List<String> row : rows) {
      for (String cell : row) {
        System.out.printf("%-15s", cell);
      }
      System.out.println();
    }
  }
}
