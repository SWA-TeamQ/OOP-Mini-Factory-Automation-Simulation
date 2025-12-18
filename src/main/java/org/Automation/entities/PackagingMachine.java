package org.automation.entities;

import java.time.LocalDateTime;

public class PackagingMachine extends Machine {

    public PackagingMachine(String id) {
        super(id);
    }

    public void activate() {
        super.activate();
        System.out.println("Packaging machine " + id + " ready.");
    }

    public void deactivate() {
        super.deactivate();
        System.out.println("Packaging machine " + id + " stopped.");
    }

    public void onTick(LocalDateTime currentTime) {
        if (isActive) {
            if (lastActionTime == null ||
                currentTime.minusSeconds(3).isAfter(lastActionTime)) {

                System.out.println("[" + id + "] Packaging items...");
                lastActionTime = currentTime;

                //the next logic will goes here ....
            }
        }
    }
}
