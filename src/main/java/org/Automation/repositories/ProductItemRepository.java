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
                    completed INTEGER DEFAULT 0
                );
                """;
    }

    @Override
    protected ProductItem mapRow(ResultSet rs) throws SQLException {
        ProductItem item = new ProductItem(rs.getString("id"));
        item.setCompleted(rs.getInt("completed") == 1);
        return item;
    }

    @Override
    public void save(ProductItem item) {
        String[] columns = {"id", "completed"};
        Object[] values = {
            item.getId(),
            item.isCompleted() ? 1 : 0
        };
        db.insert(tableName, columns, values);
    }

    public void delete(String id) {
        db.delete(tableName, "id=?", new Object[]{id});
    }
}
