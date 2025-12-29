package org.Automation.events;

/**
 * Raised when a product is assigned to a machine for processing.
 */
public class ProductAssignedToMachineEvent extends Event {
    private final String productId;
    private final String machineId;

    public ProductAssignedToMachineEvent(String productId, String machineId) {
        super("ProductAssignedToMachineEvent");
        this.productId = productId;
        this.machineId = machineId;
    }

    public String getProductId() {
        return productId;
    }

    public String getMachineId() {
        return machineId;
    }
}
