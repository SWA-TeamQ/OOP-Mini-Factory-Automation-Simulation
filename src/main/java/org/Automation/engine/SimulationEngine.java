package org.Automation.engine;

import org.Automation.Controllers.WorkFlowController;

public class SimulationEngine {

    private final WorkFlowController controller;
    private boolean running = false;

    public SimulationEngine(WorkFlowController controller) {
        this.controller = controller;
    }

    public void startSimulation() {
        SimulationClock clock = SimulationClock.getInstance();
        clock.setSpeedFactor(1);
        clock.start();
        running = true;

        System.out.println(
            "The System has started its function at time: " + clock.getCurrentTime()
        );
    }

    public void runStep() {
        if (!running) return;

        controller.runProductionStep(); // ONE logical tick
    }

    public void stopSimulation() {
        SimulationClock clock = SimulationClock.getInstance();
        clock.stop();
        running = false;

        System.out.println(
            "The System has ended its function at time: " + clock.getCurrentTime()
        );
    }

    public boolean isRunning() {
        return running;
    }
}
