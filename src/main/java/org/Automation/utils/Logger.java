package org.automation.utils;

import org.automation.database.DatabaseManager;
import org.automation.entities.ProductItem;

class Log {
    protected int id;
    protected String timestamp;
    protected String componentType;
    protected int componentId;
    protected String eventType;
    protected String message;

    public Log(int id, String timestamp, String componentType, int componentId, String eventType, String message) {
        this.id = id;
        this.timestamp = timestamp;
        this.componentType = componentType;
        this.componentId = componentId;
        this.eventType = eventType;
        this.message = message;
    }
}

public class Logger {
    private DatabaseManager db;

    public Logger(DatabaseManager db) {
        this.db = db;
    }

    public void logProduct(ProductItem item) {

    }

    public void log(String componentType, int componentId, String eventType, String message) {
        System.out.println(componentType + " [" + componentId + "] " + eventType + ": " + message);
    }

    public void logError(String error) {

    }

    public void clear() {

    }

    public void print() {

    }

    @Override
    public String toString() {
        return "Logger";
    }
}
