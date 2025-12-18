package org.automation.entities;

public abstract class Actuator {
    protected int id;
    protected String name;
    protected boolean active;

    public Actuator(int id, String name) {
        this.id = id;
        this.name = name;
        this.active = false;
    }

    public void activate() {
        active = true;
        System.out.println(this.toShortString() + " activated.");
    }

    public void deactivate() {
        active = false;
        System.out.println(this.toShortString() + " deactivated.");
    }

    public boolean isActive(){
        return active;
    }

    public int getId() {
        return id;
    }

    abstract public String toShortString();
}
