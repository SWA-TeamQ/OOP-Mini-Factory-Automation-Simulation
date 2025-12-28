package org.Automation.repositories;

import org.Automation.core.DatabaseManager;
import org.Automation.core.EntityFactory;
import org.Automation.entities.ProductItem;
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
                    id TEXT PRIMARY KEY,
                    name TEXT,
                    weight REAL,
                    status TEXT,
                    createdAt TEXT,
                    currentStationId TEXT
                );
                """;
    }

    @Override
    protected ProductItem mapRow(ResultSet rs) throws SQLException {
        return EntityFactory.createProductItem(
            rs.getString("id"),
            rs.getString("name"),
            rs.getDouble("weight"),
            rs.getString("status"),
            rs.getString("createdAt"),
            rs.getString("currentStationId")
        );
    }

    @Override
    public void save(ProductItem item) {
        String[] columns = {"id", "name", "weight", "status", "createdAt", "currentStationId"};
        Object[] values = {
            item.getId(),
            null, // name not in entity yet
            0.0,  // weight not in entity yet
            null, // status not in entity yet
            null, // createdAt not in entity yet
            null  // currentStationId not in entity yet
        };
        
        if (!db.update(tableName, "name=?, weight=?, status=?, createdAt=?, currentStationId=?", "id=?", new Object[]{values[1], values[2], values[3], values[4], values[5], values[0]})) {
            db.insert(tableName, columns, values);
        }
    }

    public void delete(String id) {
        db.delete(tableName, "id=?", new Object[]{id});
    }
}
