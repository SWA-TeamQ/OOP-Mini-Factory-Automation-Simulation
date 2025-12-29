package org.automation.ui;

import org.automation.controllers.WorkFlowController;

public class ConsoleApp {
    private WorkFlowController controller;

    public ConsoleApp(WorkFlowController controller) {
        this.controller = controller;
    }

     public void start() {
        ConsoleCoordinator coordinator = new ConsoleCoordinator();
        coordinator.launch(controller);
    }
}
