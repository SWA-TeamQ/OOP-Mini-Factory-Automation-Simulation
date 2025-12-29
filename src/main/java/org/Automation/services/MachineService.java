package org.Automation.services;

import org.Automation.entities.Machine;
import org.Automation.entities.ProductItem;
import org.Automation.repositories.MachineRepository;
import org.Automation.core.EventBus;
import org.Automation.core.Logger;

public class MachineService implements IMachineService {
    private final MachineRepository machineRepo;
    private final EventBus eventBus;

    public MachineService(MachineRepository machineRepo, EventBus eventBus) {
        this.machineRepo = machineRepo;
        this.eventBus = eventBus;
    }

    @Override
    public void startMachine(String machineId) {
        Machine machine = machineRepo.findById(machineId);
        if (machine != null) {
            machine.start();
            machineRepo.save(machine);
            Logger.info("Machine " + machineId + " started via MachineService.");
        }
    }

    @Override
    public void stopMachine(String machineId) {
        Machine machine = machineRepo.findById(machineId);
        if (machine != null) {
            machine.stop();
            machineRepo.save(machine);
            Logger.info("Machine " + machineId + " stopped via MachineService.");
        }
    }

    @Override
    public void processItem(String machineId, ProductItem item) {
        Machine machine = machineRepo.findById(machineId);
        if (machine != null) {
            if (machine.assignItem(item)) {
                machineRepo.save(machine);
                Logger.info("Item " + item.getId() + " assigned to machine " + machineId);
            } else {
                Logger.warn("Failed to assign item " + item.getId() + " to machine " + machineId + " (Machine busy or stopped)");
            }
        }
    }

    @Override
    public Machine getMachine(String machineId) {
        return machineRepo.findById(machineId);
    }
}
