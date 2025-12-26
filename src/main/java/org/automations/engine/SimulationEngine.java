package org.automations.engine;

import java.time.LocalDateTime;

import org.automations.Controllers.WorkFlowController;
import org.automations.core.Logger;

public class SimulationEngine {
  // Instance Variables

  WorkFlowController controller;

  public SimulationEngine() {
  }

  // Instance Methods
  public void runStep() {

  }

  public void startSimulation() {
    SimulationClock clock = SimulationClock.getInstance();
    clock.setSpeedFactor(1);
    clock.start();
    System.out.println("The System has started its function at time: " + clock.getCurrentTime());
  }

  public void stopSimulation() {
    SimulationClock clock = SimulationClock.getInstance();
    clock.stop();
    System.out.println("The System has ended its function at time: " + clock.getCurrentTime());
  }

  public void printSimulationStatus() {

  }
}
