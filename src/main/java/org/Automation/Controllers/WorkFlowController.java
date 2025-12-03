package org.Automation.Controllers;

import ProductionLine.ProductionLine;
import org.Automation.Controllers.Simluators.SimulationClock;

import java.time.LocalDateTime;

/**
 
 * - starts/stops production line
 * - starts/stops global SimulationClock
 * - exposes a simple onSensorEvent hook that sensors/managers can call
 */
public class WorkFlowController {

    private final ProductionLine productionLine;
    private boolean running = false;

    public WorkFlowController(ProductionLine productionLine) {
        this.productionLine = productionLine;
    }

    public synchronized void startProduction() {
        if (running) return;
        System.out.println("[WorkFlowController] Starting production...");
        try {
            productionLine.startLine();
        } catch (Exception ex) {
            System.err.println("[WorkFlowController] productionLine.startLine() error: " + ex.getMessage());
        }
        try {
            SimulationClock.getInstance().start();
        } catch (Exception ex) {
            System.err.println("[WorkFlowController] SimulationClock.start() error: " + ex.getMessage());
        }
        running = true;
    }

    public synchronized void stopProduction() {
        if (!running) return;
        System.out.println("[WorkFlowController] Stopping production...");
        try {
            productionLine.stopLine();
        } catch (Exception ex) {
            System.err.println("[WorkFlowController] productionLine.stopLine() error: " + ex.getMessage());
        }
        try {
            SimulationClock.getInstance().stop();
        } catch (Exception ex) {
            System.err.println("[WorkFlowController] SimulationClock.stop() error: " + ex.getMessage());
        }
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
