package org.automations.ui;

import org.automations.entities.Actuator;
import org.automations.services.ActuatorService;
import org.automations.ui.helpers.*;

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
