package org.automation.services;

import java.time.LocalDateTime;

/**
 * Minimal ProductionLineService scaffold implementing IProductionLineService.
 * Keeps internal running state and provides tick hook for WorkflowController.
 * Extend with real repository/logic in follow-up commits.
 */
public class ProductionLineService implements IProductionLineService {

    private volatile boolean running = false;
    private String status = "stopped";

    @Override
    public void onTick(LocalDateTime time) {
        if (!running) return;
        // TODO: advance items on the line, interact with stations, schedule actuators
        System.out.println("[ProductionLineService] tick at " + time + " — processing pending work.");
    }

    @Override
    public void start() {
        running = true;
        status = "running";
        System.out.println("[ProductionLineService] started.");
    }

    @Override
    public void stop() {
        running = false;
        status = "stopped";
        System.out.println("[ProductionLineService] stopped.");
    }

    @Override
    public void processPending() {
        // TODO: process pending work immediately (called by controller)
        System.out.println("[ProductionLineService] processPending invoked.");
    }

    @Override
    public String getStatus() {
        return status;
    }
}
