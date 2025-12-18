package org.automation.entities;

public class Actuator {

    protected String id;
    protected boolean isActive;

    public Actuator(String id) {
        this.id = id;
        this.isActive = false;
    }

    public void activate() {
        isActive = true;
        System.out.println(id + " activated.");
    }

    public void deactivate() {
        isActive = false;
        System.out.println(id + " deactivated.");
    }

    public boolean getStatus() {
        return isActive;
    }

    public String getId() {
        return id;
    }
}
