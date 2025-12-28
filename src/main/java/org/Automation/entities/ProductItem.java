package org.Automation.entities;

import java.util.ArrayList;
import java.util.List;

public class ProductItem {

    private final String id;
    private boolean completed;
    private final List<String> history = new ArrayList<>();
    private String tempSensorId;
    private String weightSensorId;

    public ProductItem(String id, String tempSensorId, String weightSensorId) {
        this.id = id;
        this.tempSensorId = tempSensorId;
        this.weightSensorId = weightSensorId;
    }
    public String getId() { return id; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void addHistory(String entry) { history.add(entry); }
    public List<String> getHistory() { return history; }
      public String getTempSensorId() { return tempSensorId; }
    public String getWeightSensorId() { return weightSensorId; }
}
