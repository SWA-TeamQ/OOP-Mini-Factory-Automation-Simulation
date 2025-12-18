package org.automation.services;

import java.time.LocalDateTime;

/**
 * Production line service contract used by WorkflowController.
 */
public interface IProductionLineService {
    void onTick(LocalDateTime time);
    void start();
    void stop();
    void processPending();
    String getStatus();
}
