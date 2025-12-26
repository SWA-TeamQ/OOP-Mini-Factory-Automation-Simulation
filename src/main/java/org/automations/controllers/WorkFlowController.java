package org.automations.controllers;

import java.time.LocalDateTime;

import org.automations.engine.ClockObserver;
import org.automations.engine.SimulationClock;
import org.automations.services.IActuatorService;
import org.automations.services.IItemTrackingService;
import org.automations.services.IProductionLineService;
import org.automations.services.ISensorService;

/**
 * Orchestrates high-level workflow. Receives simulation clock ticks and
 * delegates
 * time-based decisions to services (ProductionLine, ActuatorService,
 * SensorService).
 */
public class WorkFlowController implements ClockObserver {
  private final IProductionLineService productionLine;
  private final ISensorService sensorService;
  private final IActuatorService actuatorService;
  private final IItemTrackingService itemTracker;

  public WorkFlowController(IProductionLineService productionLine,
      ISensorService sensorService,
      IActuatorService actuatorService,
      IItemTrackingService itemTracker) {
    this.productionLine = productionLine;
    this.sensorService = sensorService;
    this.actuatorService = actuatorService;
    this.itemTracker = itemTracker;

    // register to receive clock ticks and make scheduling decisions here
    SimulationClock.getInstance().register(this);
  }

  // Simulation tick handler - keep decision logic here, actuators stay
  // simple/passive
  @Override
  public void onTick(LocalDateTime currentTime) {
    // 1) Let sensor service update/publish sensor events for this tick
    sensorService.onTick(currentTime);

    // 2) Advance production line (move items, decide station actions)
    productionLine.onTick(currentTime);

    // 3) Let actuator service process scheduled actuator actions
    actuatorService.onTick(currentTime);

    // 4) Optional: perform tracking/logging
    // itemTracker.snapshot() or similar if implemented
  }

  public void startProduction() {
    // orchestration start logic
    productionLine.start();
  }

  public void stopProduction() {
    productionLine.stop();
  }

  public void handleItemFlow() {
    // delegate to productionLine/service
    productionLine.processPending();
  }

  public void printSystemStatus() {
    System.out.println("ProductionLine status: " + productionLine.getStatus());
    actuatorService.getActuators();
  }
}
