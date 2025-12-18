package org.automation.services;

import java.time.LocalDateTime;

public interface IProductionLineService {
    void onTick(LocalDateTime time);
    void start();
    void stop();
    void processPending();
    String getStatus();
}
