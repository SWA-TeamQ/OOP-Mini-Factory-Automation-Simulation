package org.automation.entities;

import org.automation.entities.enums.MachineStatus;
import org.automation.entities.enums.MachineType;

public class PackagingMachine extends Machine {

    public PackagingMachine(int id, String name, MachineType machineType) {
        super(id, name, machineType);
        this.latency = 3; // 3 ticks
    }

    public PackagingMachine(int id, String name, MachineType machineType, MachineStatus status) {
        super(id, name, machineType, status);
        this.latency = 3; // 3 ticks
    }

    public void activate() {
        super.activate();
        System.out.println(this.toShortString() + " ready.");
    }

    public void deactivate() {
        super.deactivate();
        System.out.println(this.toShortString() + " stopped.");
    }

    @Override
    public void onTick(int secondsPassed) {
        if (this.isActive()) {
            // if (lastActionTime == null ||
            // currentTime.minusSeconds(3).isAfter(lastActionTime)) {

            // System.out.println("[" + id + "] Packaging items...");
            // lastActionTime = currentTime;

            // //the next logic will goes here ....
            // }
        }
    }

}
