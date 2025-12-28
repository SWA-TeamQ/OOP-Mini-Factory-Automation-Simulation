package org.Automation.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Simulates persistence for the mini factory.
 */
public class DatabaseManager {

    private static final String LOG_FILE = "factory_database.log";
    private BufferedWriter writer;
    private boolean active = true;

    public DatabaseManager() {
        try {
            writer = new BufferedWriter(new FileWriter(LOG_FILE, true));
            log("DatabaseManager initialized");
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize DatabaseManager", e);
        }
    }

    public synchronized void saveEvent(String eventType, String message) {
        if (!active) return;
        log("[EVENT] " + eventType + " | " + message);
    }

    public synchronized void saveProductionRecord(String record) {
        if (!active) return;
        log("[PRODUCTION] " + record);
    }

    public synchronized void saveError(String error) {
        if (!active) return;
        log("[ERROR] " + error);
    }

    private void log(String message) {
        try {
            writer.write(LocalDateTime.now() + " :: " + message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("DatabaseManager write failure: " + e.getMessage());
        }
    }

    public synchronized void shutdown() {
        if (!active) return;
        try {
            log("DatabaseManager shutting down");
            writer.close();
            active = false;
        } catch (IOException e) {
            System.err.println("Failed to close DatabaseManager: " + e.getMessage());
        }
    }
}
