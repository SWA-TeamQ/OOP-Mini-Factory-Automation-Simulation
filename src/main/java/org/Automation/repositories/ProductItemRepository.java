package org.automation.repositories;
import org.automation.core.DatabaseManager;
import org.automation.entities.ProductItem;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ProductItemRepository extends Repository<ProductItem> {
    public ProductItemRepository(DatabaseManager db) {
        super("ProductItem", db);
    }

    @Override
    public String createTableQuery() {
        return """
                    CREATE TABLE IF NOT EXISTS ProductItem (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        weight REAL,
                        status TEXT,
                        createdAt TEXT,
                        currentStationId INTEGER
                    );
                """;
    }

    @Override
    protected ProductItem mapRow(ResultSet rs) throws SQLException {
        return new ProductItem(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("weight"),
                rs.getString("status"),
                rs.getString("createdAt"),
                rs.getInt("currentStationId"));
    }
}