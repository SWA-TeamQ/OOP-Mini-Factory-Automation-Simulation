package org.Automation.services;

import org.Automation.entities.ConveyorBelt;
import org.Automation.entities.ProductItem;

public interface IConveyorService {
    void startConveyor(String conveyorId);
    void stopConveyor(String conveyorId);
    void moveItem(String conveyorId, ProductItem item);
    ConveyorBelt getConveyor(String conveyorId);
}
