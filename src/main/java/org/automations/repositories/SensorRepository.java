package org.automations.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.automations.core.DatabaseManager;
import org.automations.entities.Sensor;

public class SensorRepository extends Repository<Sensor> {
  public SensorRepository(DatabaseManager db) {
    super("Sensor", db);
  }

  @Override
  public Sensor mapRow(ResultSet rs) throws SQLException {
    return new Sensor(
        rs.getInt("id"),
        rs.getString("type"),
        rs.getInt("machineId"),
        rs.getString("status"));
  }

  @Override
  public String createTableQuery() {
    return """
            CREATE TABLE IF NOT EXISTS Sensor (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                type TEXT NOT NULL,
                machineId INTEGER,
                status TEXT DEFAULT 'inactive',
                FOREIGN KEY(machineId) REFERENCES Machine(id)
            );
        """;
  }
}
