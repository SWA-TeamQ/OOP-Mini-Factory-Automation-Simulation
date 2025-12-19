package org.automation.ui;

import org.automation.entities.Actuator;
import org.automation.services.ActuatorService;

import org.automation.ui.helpers.*;

public class ConsoleCordinator {
    private final ActuatorService actuatorService;

    public ConsoleCordinator(ActuatorService actuatorService) {
        this.actuatorService = actuatorService;
    }

    public void showActuators() {
        TableView table = new TableView();
        table.setHeader("ID", "Name", "Active");
        for (Actuator a : actuatorService.getActuators()) {
            table.addRow(a.getId(), a.getName(), a.isActive());
        }

        table.display();
    }
}
