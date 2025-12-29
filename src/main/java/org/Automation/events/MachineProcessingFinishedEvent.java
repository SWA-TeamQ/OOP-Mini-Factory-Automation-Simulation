package org.Automation.events;

import org.Automation.entities.ProductItem;

public class MachineProcessingFinishedEvent extends Event {
    private final String machineId;
    private final ProductItem productItem;

    public MachineProcessingFinishedEvent(String machineId, ProductItem productItem) {
        super("MachineProcessingFinishedEvent", machineId);
        this.machineId = machineId;
        this.productItem = productItem;
    }

    public String getMachineId() {
        return machineId;
    }

    public ProductItem getProductItem() {
        return productItem;
    }
}
