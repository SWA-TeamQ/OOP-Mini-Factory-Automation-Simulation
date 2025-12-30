package org.automation.services.interfaces;

import org.automation.entities.Machine;
import org.automation.entities.ProductItem;

public interface IMachineService {
    void startMachine(String machineId);
    void stopMachine(String machineId);
    void processItem(String machineId, ProductItem item);
    Machine getMachine(String machineId);
}
