package org.Automation.ui.helpers;

import org.Automation.entities.Station;
import org.Automation.repositories.StationRepository;

public class FactorySnapshotView {
    private final StationRepository stationRepo;

    public FactorySnapshotView(StationRepository stationRepo) {
        this.stationRepo = stationRepo;
    }

    public void displaySnapshot() {
        System.out.println("=== FACTORY SNAPSHOT ===");
        for (Station station : stationRepo.findAll()) {
            System.out.println("Station: " + station.getId() + " | Status: " + station.getStatus());
            station.getItems().forEach(item ->
                    System.out.println("  - Item: " + item.getId() + " | Completed: " + item.isCompleted()));
        }
        System.out.println("========================\n");
    }
}
