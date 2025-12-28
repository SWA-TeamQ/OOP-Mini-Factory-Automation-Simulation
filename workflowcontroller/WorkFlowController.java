package org.Automation.Controllers;

import java.time.LocalDateTime;

import org.Automation.engine.SimulationClock;
import org.Automation.engine.ClockObserver;
import org.Automation.services.IActuatorService;
import org.Automation.services.IProductionLineService;
import org.Automation.services.IItemTrackingService;
import org.Automation.services.ISensorService;

/**
 * Orchestrates high-level workflow.
 * Receives simulation clock ticks and delegates time-based decisions
 * to production line, sensors, actuators, and item tracking services.
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

        // Defensive checks (safe, no behavior change in normal use)
        if (productionLine == null || sensorService == null
                || actuatorService == null || itemTracker == null) {
            throw new IllegalArgumentException(
                "WorkFlowController dependencies must not be null");
        }

        this.productionLine = productionLine;
        this.sensorService = sensorService;
        this.actuatorService = actuatorService;
        this.itemTracker = itemTracker;

        // Register controller to receive simulation clock ticks
        SimulationClock.getInstance().register(this);
    }

    /**
     * Simulation tick handler.
     * Execution order is intentional:
     * 1. Sensors update
     * 2. Production line advances
     * 3. Actuators execute actions
     * 4. Item tracking/logging
     */
    @Override
    public void onTick(LocalDateTime currentTime) {
        sensorService.onTick(currentTime);
        productionLine.onTick(currentTime);
        actuatorService.onTick(currentTime);
        itemTracker.onTick(currentTime);
    }

    /**
     * Starts production and all subsystems.
     */
    public void startProduction() {
        productionLine.start();
        sensorService.start();
        actuatorService.start();
        itemTracker.start();

        System.out.println("[WorkFlowController] production started.");
    }

    /**
     * Stops production and all subsystems gracefully.
     */
    public void stopProduction() {
        productionLine.stop();
        sensorService.stop();
        actuatorService.stop();
        itemTracker.stop();

        System.out.println("[WorkFlowController] production stopped.");
    }

    /**
     * Delegates pending item processing to the production line.
     */
    public void handleItemFlow() {
        productionLine.processPending();
    }

    /**
     * Prints current system status (for debugging/demo purposes).
     */
    public void printSystemStatus() {
        System.out.println("ProductionLine status: " + productionLine.getStatus());
        System.out.println("SensorService status: " + sensorService.getStatus());
        System.out.println("ActuatorService status: " + actuatorService.getStatus());
        System.out.println("ItemTracker status: " + itemTracker.getStatus());
        System.out.println("Actuators: " + actuatorService.getActuators());
    }
}
