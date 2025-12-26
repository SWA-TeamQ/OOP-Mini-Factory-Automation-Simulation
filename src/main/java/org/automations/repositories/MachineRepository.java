package org.automations.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.automations.core.DatabaseManager;
import org.automations.entities.Machine;
import org.automations.entities.PackagingMachine;
import org.automations.entities.enums.MachineType;

public class MachineRepository extends Repository<Machine> {
  public MachineRepository(DatabaseManager db) {
    super("Machine", db);
  }

  @Override
  public String createTableQuery() {
    return """
            CREATE TABLE IF NOT EXISTS Machine (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                status ENUM(IDLE, STOPPED, RUNNING, ) DEFAULT 'IDLE',
        );
        """;
  }

  @Override
  protected Machine mapRow(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    String name = rs.getString("name");
    String type = rs.getString("type");
    String status = rs.getString("status");

    final MachineType typeResolved = MachineType.valueOf(type.toUpperCase());

    // FIX: You need to provide arguments for this constructor call
    // PackagingMachine machine = new PackagingMachine();

    return switch (typeResolved) {
      case MachineType.INPUT -> new PackagingMachine(id, name, MachineType.INPUT);
      case MachineType.PROCESSING -> new PackagingMachine(id, name, MachineType.PROCESSING);
      // case "PROCESSING" -> new PackagingMachine(id, name, type, status);
      default -> new PackagingMachine(id, name, MachineType.PACKAGING);
    };

    // NOTE: This was the return statement before editing
    // return new T(
    // rs.getInt("id"),
    // rs.getString("name"),
    // rs.getString("type"),
    // rs.getString("status"));
  }
}
