package org.Automation.entities;

public class EventLog {
    public int id;
    public String timestamp;
    public String componentType;
    public int componentId;
    public String eventType;
    public String message;

    public EventLog(int id, String timestamp, String componentType, int componentId, String eventType, String message) {
        this.id = id;
        this.timestamp = timestamp;
        this.componentType = componentType;
        this.componentId = componentId;
        this.eventType = eventType;
        this.message = message;
    }

    @Override
    public String toString() {
        return "EventLog{id=" + id + ", timestamp=" + timestamp + ", componentType=" + componentType + ", componentId=" + componentId + ", eventType=" + eventType + ", message=" + message + "}";
    }
}
