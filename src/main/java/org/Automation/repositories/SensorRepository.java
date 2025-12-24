package org.automation.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.automation.database.DatabaseManager;
import org.automation.entities.Sensor;
import org.automation.entities.TemperatureSensor;
import org.automation.entities.WeightSensor;

/**
 * SensorRepository
 *
 * - Maps rows to concrete Sensor implementations (TemperatureSensor or WeightSensor)
 * - Uses nullable columns for sensor-specific fields and provides sensible defaults
 * - Creates a table schema that contains common and sensor-specific columns
 */
public class SensorRepository extends Repository<Sensor> {

    public SensorRepository(DatabaseManager db) {
        super("Sensor", db);
    }

    /**
     * Ensure sensor table exists in DB
     */
    public boolean ensureTable() {
        return db.executeDDL(createTableQuery());
    }

    /**
     * Persist a sensor into the Sensor table. Inserts only (no upsert for simplicity).
     */
    public boolean save(Sensor sensor) {
        if (sensor == null) return false;
        String type = sensor.getSensorType();
        // If a row with this sensor ID exists -> UPDATE, otherwise INSERT (omit id to let SQLite autoincrement)
        Sensor existing = findOne("id = ?", new Object[]{sensor.getSensorId()});
        try {
            if (sensor instanceof TemperatureSensor) {
                TemperatureSensor t = (TemperatureSensor) sensor;
                if (existing != null) {
                    String set = "type = ?, location = ?, status = ?, startThreshold = ?, tolerance = ?, targetTemperature = ?, temperatureUnit = ?";
                    Object[] params = new Object[]{t.getSensorType(), t.getLocation(), t.getStatus(), t.getStartThreshold(), t.getTemperatureTolerance(), t.getTargetTemperature(), t.getTemperatureUnit(), t.getSensorId()};
                    return update(set, "id = ?", params);
                } else {
                    // Insert with explicit id so in-memory id and DB id stay in sync. ID collision is avoided
                    // because we already checked for an existing row.
                    String[] cols = new String[]{"id","type","location","status","startThreshold","tolerance","targetTemperature","temperatureUnit"};
                    Object[] vals = new Object[]{t.getSensorId(), t.getSensorType(), t.getLocation(), t.getStatus(), t.getStartThreshold(), t.getTemperatureTolerance(), t.getTargetTemperature(), t.getTemperatureUnit()};
                    return insert(cols, vals);
                }
            } else if (sensor instanceof WeightSensor) {
                WeightSensor w = (WeightSensor) sensor;
                if (existing != null) {
                    String set = "type = ?, location = ?, status = ?, initialWeight = ?, capacity = ?, weightUnit = ?";
                    Object[] params = new Object[]{w.getSensorType(), w.getLocation(), w.getStatus(), w.getCurrentWeight(), w.getMachineCapacity(), w.getWeightUnit(), w.getSensorId()};
                    return update(set, "id = ?", params);
                } else {
                    String[] cols = new String[]{"id","type","location","status","initialWeight","capacity","weightUnit"};
                    Object[] vals = new Object[]{w.getSensorId(), w.getSensorType(), w.getLocation(), w.getStatus(), w.getCurrentWeight(), w.getMachineCapacity(), w.getWeightUnit()};
                    return insert(cols, vals);
                }
            } else {
                if (existing != null) {
                    String set = "type = ?, location = ?, status = ?";
                    Object[] params = new Object[]{sensor.getSensorType(), sensor.getLocation(), sensor.getStatus(), sensor.getSensorId()};
                    return update(set, "id = ?", params);
                } else {
                    String[] cols = new String[]{"id","type","location","status"};
                    Object[] vals = new Object[]{sensor.getSensorId(), sensor.getSensorType(), sensor.getLocation(), sensor.getStatus()};
                    return insert(cols, vals);
                }
            }
        } catch (RuntimeException re) {
            throw re;
        }
    }

    public boolean deleteById(int id) {
        return delete("id = ?", new Object[]{id});
    }

    /**
     * Map a ResultSet row to a concrete Sensor instance.
     * Expects the table to contain columns for both temperature and weight sensors.
     */
    @Override
    public Sensor mapRow(ResultSet rs) throws SQLException {
        int id = 0;
        try { id = rs.getInt("id"); } catch (SQLException ignored) {}
        String type = safeGetString(rs, "type", "Unknown");
        String location = safeGetString(rs, "location", "Unknown");
        String status = safeGetString(rs, "status", "inactive");

        // Temperature-specific columns (may be null for non-temperature rows)
        double startThreshold = safeGetDouble(rs, "startThreshold", 0.0);
        double tolerance = safeGetDouble(rs, "tolerance", 1.0);
        double targetTemperature = safeGetDouble(rs, "targetTemperature", 25.0);
        String tempUnit = safeGetString(rs, "temperatureUnit", "°C");

        // Weight-specific columns (may be null for non-weight rows)
        double initialWeight = safeGetDouble(rs, "initialWeight", 0.0);
        double capacity = safeGetDouble(rs, "capacity", 100.0);
        String weightUnit = safeGetString(rs, "weightUnit", "kg");

        // Choose concrete type based on the 'type' column (case-insensitive)
        if ("temperature".equalsIgnoreCase(type) || type.toLowerCase().contains("temp")) {
            return new TemperatureSensor(
                id,
                type,
                location,
                status,
                startThreshold,
                tolerance,
                targetTemperature,
                tempUnit
            );
        } else if ("weight".equalsIgnoreCase(type)) {
            return new WeightSensor(
                id,
                type,
                location,
                status,
                initialWeight,
                capacity,
                weightUnit
            );
        } else {
            // Fallback: return a TemperatureSensor with safe defaults to avoid nulls
            return new TemperatureSensor(
                id,
                type,
                location,
                status,
                startThreshold,
                tolerance,
                targetTemperature,
                tempUnit
            );
        }
    }

    /**
     * Table schema includes common columns plus nullable sensor-specific columns.
     * This keeps a single Sensor table compatible with both TemperatureSensor and WeightSensor.
     */
    @Override
    public String createTableQuery() {
        return """
            CREATE TABLE IF NOT EXISTS Sensor (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                type TEXT NOT NULL,
                location TEXT,
                machineId INTEGER,
                status TEXT DEFAULT 'inactive',

                -- Temperature sensor fields (nullable)
                startThreshold REAL,
                tolerance REAL,
                targetTemperature REAL,
                temperatureUnit TEXT,

                -- Weight sensor fields (nullable)
                initialWeight REAL,
                capacity REAL,
                weightUnit TEXT,

                FOREIGN KEY(machineId) REFERENCES Machine(id)
            );
            """;
    }

    // -----------------------
    // Helper getters with defaults
    // -----------------------
    private String safeGetString(ResultSet rs, String column, String defaultValue) {
        try {
            String v = rs.getString(column);
            return v == null ? defaultValue : v;
        } catch (SQLException e) {
            return defaultValue;
        }
    }

    private double safeGetDouble(ResultSet rs, String column, double defaultValue) {
        try {
            double v = rs.getDouble(column);
            return rs.wasNull() ? defaultValue : v;
        } catch (SQLException e) {
            return defaultValue;
        }
    }
}
