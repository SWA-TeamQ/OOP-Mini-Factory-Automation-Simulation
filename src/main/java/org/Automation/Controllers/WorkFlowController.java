package org.Automation.Controllers;

import java.time.LocalDateTime;
import org.Automation.engine.SimulationClock;
import org.Automation.engine.ClockObserver;
import org.Automation.services.IActuatorService;
import org.Automation.services.IProductionvLineService;
import org.Automation.services.IItemTrackingService;
import org.Automation.services.ISensorService;

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

    // 4) Track/log item movement or snapshots
    itemTracker.onTick(currentTime);
  }

  public void startProduction() {
    // orchestration start logic
    sensorService.start();
    productionLine.start();
    itemTracker.start();
  }

  public void stopProduction() {
    sensorService.stop();
    productionLine.stop();
    itemTracker.stop();
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
