package org.automation;

import org.automation.controllers.ItemTracker;
import org.automation.entities.ProductItem;
import org.automation.database.DatabaseManager;
import org.automation.utils.Logger;

public class Main {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();
        db.connect();
        
        // cleanup: disconnect DB
        db.disconnect();
    }
}