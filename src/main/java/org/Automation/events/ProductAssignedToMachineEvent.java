package org.automation.events;

import org.automation.events.abstracts.ProductEvent;
import org.automation.entities.ProductItem;

/**
 * Raised when a product is assigned to a machine for processing.
 */
public class ProductAssignedToMachineEvent extends ProductEvent {
    private final String machineId;

    public ProductAssignedToMachineEvent(ProductItem productItem, String machineId) {
        super("ProductAssignedToMachineEvent", productItem);
        this.machineId = machineId;
    }

    public String getMachineId() {
        return machineId;
    }

    @Override
    public String toString() {
        return "Product " + getProductId() + " assigned to machine " + machineId;
    }
}
