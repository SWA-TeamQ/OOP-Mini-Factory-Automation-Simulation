package org.Automation.repositories;

import org.Automation.entities.ProductItem;

public class ProductItemRepository extends Repository<ProductItem> {

    @Override
    public void save(ProductItem item) { add(item.getId(), item); }

    @Override
    public void delete(String id) { remove(id); }
}
