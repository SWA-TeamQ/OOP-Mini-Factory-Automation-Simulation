package org.automation.services;

import org.automation.entities.Machine;

public interface IMachineService {
    void startMachine(String machineId);
    void stopMachine(String machineId);
    void processItem(String machineId, org.automation.entities.ProductItem item);
    Machine getMachine(String machineId);
}
