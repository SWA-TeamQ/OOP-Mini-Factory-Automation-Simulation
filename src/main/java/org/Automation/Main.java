package org.automation;

import org.automation.controllers.ItemTracker;
import org.automation.core.DatabaseManager;
import org.automation.core.Logger;
import org.automation.entities.ProductItem;

public class Main {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();
        db.connect();
        
        // cleanup: disconnect DB
        db.disconnect();
    }
}