package org.Automation.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product item in the production line.
 * Tracks lifecycle data: start time, end time, and defective status.
 */
public class ProductItem {

    private final String id;
    private boolean completed;
    private boolean defective;
    private long startTick;
    private long endTick;
    private final List<String> history = new ArrayList<>();

    // Sensor values for display
    private double temperature = 20.0; // Default ambient
    private double weight = 10.0; // Default nominal

    public ProductItem(String id) {
        this.id = id;
        this.completed = false;
        this.defective = false;
        this.startTick = 0;
        this.endTick = 0;
    }

    public String getId() {
        return id;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isDefective() {
        return defective;
    }

    public void setDefective(boolean defective) {
        this.defective = defective;
    }

    public long getStartTick() {
        return startTick;
    }

    public void setStartTick(long startTick) {
        this.startTick = startTick;
    }

    public long getEndTick() {
        return endTick;
    }

    public void setEndTick(long endTick) {
        this.endTick = endTick;
    }

    public long getTotalDuration() {
        if (endTick > 0 && startTick > 0) {
            return endTick - startTick;
        }
        return 0;
    }

    public void addHistory(String entry) {
        history.add(entry);
    }

    public List<String> getHistory() {
        return history;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
