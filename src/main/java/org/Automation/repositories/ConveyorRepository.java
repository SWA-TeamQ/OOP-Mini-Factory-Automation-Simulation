package org.Automation.repositories;

import org.Automation.core.DatabaseManager;
import org.Automation.entities.ConveyorBelt;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConveyorRepository extends Repository<ConveyorBelt> {
  public ConveyorRepository(DatabaseManager db) {
    super("ConveyorBelt", db);
  }

  @Override
  public ConveyorBelt mapRow(ResultSet rs) throws SQLException {
    return new ConveyorBelt(
        rs.getInt("id"),
        rs.getString("name"),
        rs.getInt("length"),
        rs.getString("status"),
        rs.getDouble("speed"),
        rs.getInt("startMachineId"),
        rs.getInt("endMachineId"));
  }

  @Override
  public String createTableQuery() {
    return """
            CREATE TABLE IF NOT EXISTS ConveyorBelt (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                length INTEGER,
                status TEXT DEFAULT 'stopped',
                speed REAL,
                startMachineId INTEGER,
                endMachineId INTEGER,
                FOREIGN KEY(startMachineId) REFERENCES Machine(id),
                FOREIGN KEY(endMachineId) REFERENCES Machine(id)
            );
        """;
  }
}
