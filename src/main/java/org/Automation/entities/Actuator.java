package org.automation.entities;

public abstract class Actuator {
    protected int id;
    protected String name;
    protected boolean active;
    protected double latency;
    protected double lastActionTime;

    public Actuator(int id, String name, double latency) {
        this.id = id;
        this.name = name;
        this.active = false;
        this.latency = latency;
        this.lastActionTime = -latency; // so that the first action can be done immediately
    }

    public void start() {
        activate();
        System.out.println(this.toShortString() + " started.");
    }

    public void stop() {
        deactivate();
        System.out.println(this.toShortString() + " stopped.");
    }

    public abstract void performAction(double currentSecond);

    public boolean canPerformAction(double currentSecond) {
        return active && (currentSecond - lastActionTime >= latency);
    }

    public void updateLastActionTime(double currentSecond) {
        lastActionTime = currentSecond;
    }

    public void activate() {
        active = true;
        System.out.println(this.toShortString() + " activated.");
    }

    public void deactivate() {
        active = false;
        System.out.println(this.toShortString() + " deactivated.");
    }

    public boolean isActive() {
        return active;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    abstract public String toShortString();
}
