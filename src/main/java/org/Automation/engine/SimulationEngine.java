package org.Automation.engine;

import org.Automation.entities.ProductItem;
import org.Automation.repositories.StationRepository;
import org.Automation.services.ProductionLineService;
import org.Automation.core.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulationEngine {
    private SimulationClock clock;
    private ProductionLineService productionLineService;
    private StationRepository stationRepo;
    private ScheduledExecutorService executor;

    public SimulationEngine(SimulationClock clock,ProductionLineService productionLineService) {
        this.productionLineService = productionLineService;
        this.clock=clock;
    }
    public SimulationEngine(ProductionLineService productionLineService) {
        this.productionLineService = productionLineService;
    }

    public SimulationEngine(SimulationClock clock,
                            ProductionLineService productionLineService,
                            StationRepository stationRepo) {
        this.clock = clock;
        this.productionLineService = productionLineService;
        this.stationRepo = stationRepo;
    }

    public void start() {
        Logger.info("Starting simulation engine...");
        executor = Executors.newSingleThreadScheduledExecutor();

        // Start the clock
        clock.start();

        // Run the production cycle every tick
        executor.scheduleAtFixedRate(() -> {
            productionLineService.startProductionCycle();
            Logger.debug("Simulation tick processed.");
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        Logger.info("Stopping simulation engine...");
        clock.stop();
        if (executor != null) executor.shutdown();
    }
}
