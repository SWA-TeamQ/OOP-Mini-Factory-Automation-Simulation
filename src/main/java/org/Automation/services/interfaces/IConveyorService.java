package org.automation.services.interfaces;

import org.automation.entities.ConveyorBelt;
import org.automation.entities.ProductItem;

public interface IConveyorService {
    void startConveyor(String conveyorId);
    void stopConveyor(String conveyorId);
    void moveItem(String conveyorId, ProductItem item);
    ConveyorBelt getConveyor(String conveyorId);
}
