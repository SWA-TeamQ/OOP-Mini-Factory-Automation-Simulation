package org.Automation.ui.helpers;

import java.util.ArrayList;
import java.util.List;

public class EventLog {
    private final List<String> logs = new ArrayList<>();

    public void add(String message) {
        logs.add(message);
        System.out.println("[EVENT] " + message);
    }

    public List<String> getLogs() {
        return logs;
    }

    public void clear() {
        logs.clear();
    }
}
