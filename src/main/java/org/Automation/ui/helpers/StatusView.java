package org.Automation.ui.helpers;

import org.Automation.entities.Machine;
import org.Automation.repositories.MachineRepository;

public class StatusView {
    private final MachineRepository machineRepo;

    public StatusView(MachineRepository machineRepo) {
        this.machineRepo = machineRepo;
    }

    public void displayMachineStatus() {
        System.out.println("=== MACHINE STATUS ===");
        for (Machine machine : machineRepo.findAll()) {
            System.out.println("Machine: " + machine.getId() +
                    " | Type: " + machine.getType() +
                    " | Status: " + machine.getStatus());
        }
        System.out.println("======================\n");
    }
}
