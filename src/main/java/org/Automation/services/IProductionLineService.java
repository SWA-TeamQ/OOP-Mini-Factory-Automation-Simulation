package org.automation.services;

import org.automation.entities.ProductItem;

public interface IProductionLineService {
    void process(ProductItem item);
}
