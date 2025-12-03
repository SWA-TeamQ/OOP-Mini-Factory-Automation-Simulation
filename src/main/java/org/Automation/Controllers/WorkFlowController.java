package org.Automation.Controllers;

import ProductionLine.ProductionLine;

import org.Automation.Controllers.Simluators.SimulationClock;
import org.Automation.Controllers.Simluators.SimulationEngine;

import java.time.LocalDateTime;

/**
 
 
 * - exposes a simple onSensorEvent hook that sensors/managers can call
 */
public class WorkFlowController {

    private final ProductionLine productionLine;
    private final SimulationEngine simulationEngine;
    private boolean running = false;

   public WorkFlowController(ProductionLine productionLine, SimulationEngine simulationEngine) {
        this.productionLine = productionLine;
        this.simulationEngine = simulationEngine;
    }

     public synchronized void startProduction() {
        if (running) return;

        System.out.println("[WorkFlowController] Starting production...");
        productionLine.startLine();
        simulationEngine.startSimulation();   
        running = true;
    }

    public synchronized void stopProduction() {
        if (!running) return;

        System.out.println("[WorkFlowController] Stopping production...");
        productionLine.stopLine();
        simulationEngine.stopSimulation();  
        running = false;
    }

    /**
     * Called by SensorManager or Sensor when an event occurs.
     * Minimal: print event and cause the line to run one step.
     */
    public void onSensorEvent(String sensorId, String itemId, LocalDateTime time) {
        System.out.printf("[WorkFlowController] SensorEvent: sensor=%s item=%s time=%s%n",
                sensorId, itemId, time);
        try {
            productionLine.runStep();
        } catch (Exception ex) {
            System.err.println("[WorkFlowController] runStep error: " + ex.getMessage());
        }
    }

    public boolean isRunning() {
        return running;
    }
}
