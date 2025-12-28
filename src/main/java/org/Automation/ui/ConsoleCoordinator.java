package org.Automation.ui;

import org.Automation.Controllers.WorkFlowController;
import org.Automation.ui.helpers.EventLog;
import org.Automation.ui.helpers.FactorySnapshotView;
import org.Automation.ui.helpers.StatusView;

public class ConsoleCoordinator {

    public void launch(WorkFlowController controller) {
        EventLog eventLog = new EventLog();
        FactorySnapshotView snapshotView = new FactorySnapshotView(controller.getStationRepository());
        StatusView statusView = new StatusView(controller.getMachineRepository());

        ConsoleUI ui = new ConsoleUI(controller, eventLog, snapshotView, statusView);
        ui.start();
    }
}
