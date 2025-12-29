package org.automation.ui.helpers;

import java.util.List;

import org.automation.entities.ProductItem;

public class TableView {

    public void displayItems(List<ProductItem> items) {
        System.out.println("=== PRODUCT ITEMS ===");
        System.out.printf("%-10s | %-10s | %-30s\n", "ID", "Completed", "History");
        System.out.println("-----------------------------------------------");
        for (ProductItem item : items) {
            System.out.printf("%-10s | %-10s | %-30s\n",
                    item.getId(), item.isCompleted(), String.join(", ", item.getHistory()));
        }
        System.out.println("====================\n");
    }
}
