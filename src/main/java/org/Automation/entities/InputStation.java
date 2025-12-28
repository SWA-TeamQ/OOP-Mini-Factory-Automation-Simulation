package org.Automation.entities;

import org.Automation.entities.enums.StationStatus;

public class InputStation extends Station {

    public InputStation(String id) {
        super(id);
        this.status = StationStatus.ACTIVE;
    }

    // Produces new items into the production line
   public ProductItem createItem(String productId, String tempSensorId, String weightSensorId) {
    ProductItem newItem = new ProductItem(productId, tempSensorId, weightSensorId);
    addItem(newItem);
    return newItem;
}


    @Override
    public void processItems() {
        // InputStation just produces items, no real processing
    }
}
