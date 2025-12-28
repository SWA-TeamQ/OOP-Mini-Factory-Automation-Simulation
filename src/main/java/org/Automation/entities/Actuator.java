package org.Automation.entities;

public class Actuator {

    private boolean active;

    public void activate() { active = true; }
    public void deactivate() { active = false; }
    public boolean isActive() { return active; }
}
