package org.Automation.entities;

import java.time.LocalDateTime;

import org.Automation.entities.enums.MachineStatus;
import org.Automation.entities.enums.MachineType;

public class PackagingMachine extends Machine {

  public PackagingMachine(int id, String name, MachineType machineType) {
    super(id, name, machineType, 3);
    this.latency = 3; // 3 ticks
  }

  public PackagingMachine(int id, String name, MachineType machineType, MachineStatus status) {
    super(id, name, machineType, 3, status);
    this.latency = 3; // 3 ticks
  }

  public void activate() {
    super.activate();
    System.out.println(this.toShortString() + " ready.");
  }

  public void deactivate() {
    super.deactivate();
    System.out.println(this.toShortString() + " stopped.");
  }

  @Override
  public void performAction(LocalDateTime currentSecond) {
    if (canPerformAction(currentSecond)) {
      System.out.println("[" + id + "] Packaging items...");
      updateLastActionTime(currentSecond);
    }
  }

  @Override
  public void onTick(int secondsPassed) {
    if (this.isActive()) {
      // if (lastActionTime == null ||
      // currentTime.minusSeconds(3).isAfter(lastActionTime)) {

      // System.out.println("[" + id + "] Packaging items...");
      // lastActionTime = currentTime;

      // //the next logic will goes here ....
      // }
    }
  }

}
