package org.Automation.ui;

import org.Automation.Controllers.WorkFlowController;
import org.Automation.repositories.*;
import org.Automation.core.EventBus;

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
