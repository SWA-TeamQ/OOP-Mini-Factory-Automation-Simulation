package org.Automation.repositories;

import org.Automation.core.DatabaseManager;
import org.Automation.entities.ProductItem;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository for ProductItem entities.
 * Stores lifecycle data: startTick, endTick, totalDuration, defective status.
 */
public class ProductItemRepository extends Repository<ProductItem> {

    public ProductItemRepository(DatabaseManager db) {
        super("ProductItem", db);
    }

    @Override
    public void ensureTableExists() {
        super.ensureTableExists();
        tryAddColumn("completed", "INTEGER DEFAULT 0");
        tryAddColumn("defective", "INTEGER DEFAULT 0");
        tryAddColumn("startTick", "INTEGER DEFAULT 0");
        tryAddColumn("endTick", "INTEGER DEFAULT 0");
        tryAddColumn("totalDuration", "INTEGER DEFAULT 0");
    }

    private void tryAddColumn(String colName, String typeDef) {
        try (java.sql.Statement stmt = db.getConnection().createStatement()) {
            stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + colName + " " + typeDef);
        } catch (SQLException e) {
            // Ignore if column exists or other specific error
            // SQLite error for duplicate column is usually "duplicate column name: ..."
        }
    }

    @Override
    public String createTableQuery() {
        return """
                CREATE TABLE IF NOT EXISTS ProductItem (
                    id TEXT PRIMARY KEY,
                    completed INTEGER DEFAULT 0,
                    defective INTEGER DEFAULT 0,
                    startTick INTEGER DEFAULT 0,
                    endTick INTEGER DEFAULT 0,
                    totalDuration INTEGER DEFAULT 0
                );
                """;
    }

    @Override
    protected ProductItem mapRow(ResultSet rs) throws SQLException {
        ProductItem item = new ProductItem(rs.getString("id"));
        item.setCompleted(rs.getInt("completed") == 1);
        item.setDefective(rs.getInt("defective") == 1);
        item.setStartTick(rs.getLong("startTick"));
        item.setEndTick(rs.getLong("endTick"));
        return item;
    }

    @Override
    public void save(ProductItem item) {
        String[] columns = { "id", "completed", "defective", "startTick", "endTick", "totalDuration" };
        Object[] values = {
                item.getId(),
                item.isCompleted() ? 1 : 0,
                item.isDefective() ? 1 : 0,
                item.getStartTick(),
                item.getEndTick(),
                item.getTotalDuration()
        };
        db.insert(tableName, columns, values);
    }

    public void delete(String id) {
        db.delete(tableName, "id=?", new Object[] { id });
    }
}
