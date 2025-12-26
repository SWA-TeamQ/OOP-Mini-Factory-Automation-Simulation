package org.automations.entities;

import java.time.LocalDateTime;

public abstract class Actuator {
  protected int id;
  protected String name;
  protected boolean active;
  protected long latency;
  protected LocalDateTime lastActionTime;

  public Actuator(int id, String name, long latency) {
    this.id = id;
    this.name = name;
    this.active = false;
    this.latency = latency;
    this.lastActionTime = LocalDateTime.now().minusSeconds(latency); // so that the first action can be done immediately
  }

  public void start() {
    activate();
    System.out.println(this.toShortString() + " started.");
  }

  public void stop() {
    deactivate();
    System.out.println(this.toShortString() + " stopped.");
  }

  public abstract void performAction(LocalDateTime currentSecond);

  public boolean canPerformAction(LocalDateTime currentSecond) {
    return active && (currentSecond.getSecond() - lastActionTime.getSecond() >= latency);
  }

  public void updateLastActionTime(LocalDateTime currentSecond) {
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
