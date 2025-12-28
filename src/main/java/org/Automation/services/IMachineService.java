package org.Automation.services;

import org.Automation.entities.Machine;

public interface IMachineService {
    void startMachine(String machineId);
    void stopMachine(String machineId);
    void processItem(String machineId, org.Automation.entities.ProductItem item);
    Machine getMachine(String machineId);
}
