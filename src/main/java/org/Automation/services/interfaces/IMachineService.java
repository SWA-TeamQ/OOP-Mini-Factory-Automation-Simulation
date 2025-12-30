package org.automation.services.interfaces;

import org.automation.entities.ProductItem;
import org.automation.entities.abstracts.Machine;

public interface IMachineService {
    void startMachine(String machineId);
    void stopMachine(String machineId);
    void processItem(String machineId, ProductItem item);
    Machine getMachine(String machineId);
}
