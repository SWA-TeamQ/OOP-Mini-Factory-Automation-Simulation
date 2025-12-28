package org.Automation.ui.helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventLog {
    private static final String LOG_FILE = "simulation.log";

    public void add(String message) {
        // Now handled by Logger.java directly
    }

    public List<String> getLogs() {
        List<String> logs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logs.add(line);
            }
        } catch (IOException e) {
            // If file doesn't exist yet, just return empty list
        }
        return logs;
    }

    public void clear() {
        // Optionally clear the file
    }
}
