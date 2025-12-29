package org.automation.services.interfaces;

import org.automation.entities.ProductItem;

public interface IProductionLineService {
    void process(ProductItem item);
}
