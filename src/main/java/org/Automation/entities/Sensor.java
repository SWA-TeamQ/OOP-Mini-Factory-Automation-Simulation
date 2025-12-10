package org.automation.entities;

public abstract class Sensor {
    private static int idCounter = 0;
    
    protected int sensorId;
    protected String sensorType;
    protected String location;
    protected String status;
    protected double currentValue;
    protected boolean isActive;

    // Constructor with auto-increment ID
    public Sensor(String sensorType, String location, String status) {
        this.sensorId = ++idCounter;
        this.sensorType = sensorType;
        this.location = location;
        this.status = status;
        this.isActive = false;
        this.currentValue = 0.0;
    }

    // Constructor with manual ID
    public Sensor(int sensorId, String sensorType, String location, String status) {
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.location = location;
        this.status = status;
        this.isActive = false;
        this.currentValue = 0.0;
        if (sensorId > idCounter) {
            idCounter = sensorId;
        }
    }

    // Abstract methods from diagram
    public abstract void readValue();
    public abstract void calibrateSensor();
    public abstract boolean validateReading();
    public abstract void sendAlert();
    public abstract void updateValue();
    public abstract Object getValue();
    
    // Concrete methods
    public void activateSensor() {
        this.isActive = true;
        this.status = "Active";
        System.out.println("Sensor " + sensorId + " activated");
    }
    
    public void deactivateSensor() {
        this.isActive = false;
        this.status = "Inactive";
        System.out.println("Sensor " + sensorId + " deactivated");
    }
    
    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }
    
    // Getters and Setters
    public int getSensorId() { return sensorId; }
    public String getSensorType() { return sensorType; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
    public double getCurrentValue() { return currentValue; }
    public boolean isActive() { return isActive; }
    
    public void setSensorId(int sensorId) { this.sensorId = sensorId; }
    public void setSensorType(String sensorType) { this.sensorType = sensorType; }
    public void setLocation(String location) { this.location = location; }
    public void setStatus(String status) { this.status = status; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }
    public void setActive(boolean active) { this.isActive = active; }

    @Override
    public String toString() {
        return "Sensor{id=" + sensorId + ", type=" + sensorType + ", location=" + location +
               ", status=" + status + ", value=" + currentValue + ", active=" + isActive + "}";
    }
}
