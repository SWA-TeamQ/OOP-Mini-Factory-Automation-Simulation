package org.automation.ui;

import java.util.*;

class TableView {
    protected List<String> headers = new ArrayList<String>();
    protected List<List<String>> rows = new ArrayList<>();

    public void setHeader(String... headerItems) {
        headers.clear();
        headers.addAll(Arrays.asList(headerItems));
    }

    public void addRow(String... rowItems) {
        if (rowItems.length != headers.size()) {
            System.out.println("Row length doesn't match header length");
            return;
        }
        rows.add(Arrays.asList(rowItems));
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

class StatusView {
    private Map<String, String> values = new LinkedHashMap<>();

    public void set(String key, String value) {
        values.put(key, value);
    }

    public void display() {
        values.forEach((key, value) -> System.out.println(String.format("%-20s : %s", key, value)));
    }
}

class EventLogView {
    private Queue<String> events = new LinkedList<>();
    private int maxSize = 10;

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

class MenuView {
    public void display() {

    }
}

class FactorySnapshotView {
    public void display() {

    }
}