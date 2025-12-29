package org.automation.ui;

import org.automation.controllers.WorkFlowController;
import org.automation.ui.helpers.EventLog;
import org.automation.ui.helpers.FactorySnapshotView;
import org.automation.ui.helpers.StatusView;

public class ConsoleCoordinator {

    public void launch(WorkFlowController controller) {
        EventLog eventLog = new EventLog();
        FactorySnapshotView snapshotView = new FactorySnapshotView(controller.getStationRepository());
        StatusView statusView = new StatusView(controller.getMachineRepository());

        ConsoleUI ui = new ConsoleUI(controller, eventLog, snapshotView, statusView);
        ui.start();
    }
}
