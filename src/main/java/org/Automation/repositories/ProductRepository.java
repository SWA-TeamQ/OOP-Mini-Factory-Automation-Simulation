package org.Automation.repositories;
import org.Automation.entities.Product;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.Automation.DatabaseManager;

public class ProductRepository extends Repository<Product> {
    public ProductRepository(DatabaseManager db) {
        super("Product", db);
    }

    @Override
    public String createTableQuery() {
        return """
                    CREATE TABLE IF NOT EXISTS Product (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        status TEXT
                    );
                """;
    }

    @Override
    protected Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("status"));
    }
}