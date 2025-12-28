package org.Automation.repositories;

import org.Automation.core.DatabaseManager;
import org.Automation.core.EntityFactory;
import org.Automation.entities.ProductItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProductItemRepository extends Repository<ProductItem> {

    public ProductItemRepository(DatabaseManager db) {
        super("ProductItem", db);
    }

    @Override
    public String createTableQuery() {
        // Updated table with tempSensorId and weightSensorId
        return """
                CREATE TABLE IF NOT EXISTS ProductItem (
                    id TEXT PRIMARY KEY,
                    tempSensorId TEXT,
                    weightSensorId TEXT,
                    status TEXT,
                    createdAt TEXT,
                    currentStationId TEXT
                );
                """;
    }

    @Override
    protected ProductItem mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String tempSensorId = rs.getString("tempSensorId");
        String weightSensorId = rs.getString("weightSensorId");
        return EntityFactory.createProductItem(id, tempSensorId, weightSensorId);
    }

    @Override
    public void save(ProductItem item) {
        String[] columns = {"id", "tempSensorId", "weightSensorId", "status", "createdAt", "currentStationId"};
        Object[] values = {
                item.getId(),
                item.getTempSensorId(),
                item.getWeightSensorId(),
                null, // status not tracked yet
                null, // createdAt not tracked yet
                null  // currentStationId not tracked yet
        };

        // Try updating first
        boolean updated = db.update(tableName,
                "tempSensorId=?, weightSensorId=?, status=?, createdAt=?, currentStationId=?",
                "id=?",
                new Object[]{values[1], values[2], values[3], values[4], values[5], values[0]});

        // Insert if update fails
        if (!updated) {
            db.insert(tableName, columns, values);
        }
    }

    public void delete(String id) {
        db.delete(tableName, "id=?", new Object[]{id});
    }

    public List<ProductItem> findAll() {
        return super.findAll(); // uses mapRow for each result
    }
}
