package org.automations.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.automations.core.DatabaseManager;
import org.automations.entities.InputStation;
import org.automations.entities.PackagingStation;
import org.automations.entities.ProductionStation;
import org.automations.entities.Station;
import org.automations.entities.enums.MachineType;
import org.automations.entities.enums.StationStatus;

public class StationRepository extends Repository<Station> {

    public StationRepository(DatabaseManager db) {
        super("Station", db);
    }

    @Override
    public String createTableQuery() {
        return """
                CREATE TABLE IF NOT EXISTS Station (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    type TEXT NOT NULL,
                    status TEXT DEFAULT 'IDLE'
                );
                """;
    }

    @Override
    protected Station mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String typeStr = rs.getString("type");
        String statusStr = rs.getString("status");

        // Convert the String from DB to your Java Enum
        final MachineType typeResolved = MachineType.valueOf(typeStr.toUpperCase());

        // Polimorphic Mapping: Create the specific subclass based on the type
        Station station = switch (typeResolved) {
            case INPUT -> new InputStation(id, name);
            case PROCESSING -> new ProductionStation(id, name);
            case PACKAGING -> new PackagingStation(id, name);
            default -> throw new IllegalArgumentException("Unsupported Station Type: " + typeResolved);
        };

        // Restore the status from the database
        try {
            station.setStatus(StationStatus.valueOf(statusStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            station.setStatus(StationStatus.IDLE);
        }

        return station;
    }
}
