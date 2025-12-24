package org.automation.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.automation.database.DatabaseManager;
import org.automation.entities.Sensor;
import org.automation.entities.TemperatureSensor;
import org.automation.entities.WeightSensor;

public class SensorRepository extends Repository<Sensor> {

    public SensorRepository(DatabaseManager db) {
        super("Sensor", db);
    }

    public boolean ensureTable() {
        boolean created = db.executeDDL(createTableQuery());
        if (created) System.out.println("Sensor table ensured/created.");
        return created;
    }

    public boolean save(Sensor sensor) {
        if (sensor == null) return false;

        boolean exists = findOne("id = ?", new Object[]{sensor.getSensorId()}) != null;

        if (sensor instanceof TemperatureSensor t) {
            return exists ? updateTemperature(t) : insertTemperature(t);
        } else if (sensor instanceof WeightSensor w) {
            return exists ? updateWeight(w) : insertWeight(w);
        } else {
            return exists ? updateBase(sensor) : insertBase(sensor);
        }
    }

    /* ---------------- INSERT ---------------- */

    private boolean insertTemperature(TemperatureSensor t) {
        return insert(
            new String[]{"type", "location", "status", "startThreshold", "tolerance", "targetTemperature", "temperatureUnit"},
            new Object[]{t.getSensorType(), t.getLocation(), t.getStatus(), t.getStartThreshold(), t.getTemperatureTolerance(), t.getTargetTemperature(), t.getTemperatureUnit()}
        );
    }

    private boolean insertWeight(WeightSensor w) {
        return insert(
            new String[]{"type", "location", "status", "initialWeight", "capacity", "weightUnit"},
            new Object[]{w.getSensorType(), w.getLocation(), w.getStatus(), w.getCurrentWeight(), w.getMachineCapacity(), w.getWeightUnit()}
        );
    }

    private boolean insertBase(Sensor s) {
        return insert(
            new String[]{"type", "location", "status"},
            new Object[]{s.getSensorType(), s.getLocation(), s.getStatus()}
        );
    }

    /* ---------------- UPDATE ---------------- */

    private boolean updateTemperature(TemperatureSensor t) {
        return update(
            "type = ?, location = ?, status = ?, startThreshold = ?, tolerance = ?, targetTemperature = ?, temperatureUnit = ?",
            "id = ?",
            new Object[]{t.getSensorType(), t.getLocation(), t.getStatus(), t.getStartThreshold(), t.getTemperatureTolerance(), t.getTargetTemperature(), t.getTemperatureUnit(), t.getSensorId()}
        );
    }

    private boolean updateWeight(WeightSensor w) {
        return update(
            "type = ?, location = ?, status = ?, initialWeight = ?, capacity = ?, weightUnit = ?",
            "id = ?",
            new Object[]{w.getSensorType(), w.getLocation(), w.getStatus(), w.getCurrentWeight(), w.getMachineCapacity(), w.getWeightUnit(), w.getSensorId()}
        );
    }

    private boolean updateBase(Sensor s) {
        return update(
            "type = ?, location = ?, status = ?",
            "id = ?",
            new Object[]{s.getSensorType(), s.getLocation(), s.getStatus(), s.getSensorId()}
        );
    }

    public boolean deleteById(int id) {
        return delete("id = ?", new Object[]{id});
    }

    /* ---------------- ROW MAPPING ---------------- */

    @Override
    public Sensor mapRow(ResultSet rs) throws SQLException {
        int sensorId = rs.getInt("id");
        String type = safeGetString(rs, "type", "");
        String location = safeGetString(rs, "location", "Unknown");
        String status = safeGetString(rs, "status", "inactive");

        return switch (type.toLowerCase()) {
            case "temperature" -> new TemperatureSensor(
                sensorId, type, location, status,
                safeGetDouble(rs, "startThreshold", 0),
                safeGetDouble(rs, "tolerance", 1),
                safeGetDouble(rs, "targetTemperature", 25),
                safeGetString(rs, "temperatureUnit", "°C")
            );
            case "weight" -> new WeightSensor(
                sensorId, type, location, status,
                safeGetDouble(rs, "initialWeight", 0),
                safeGetDouble(rs, "capacity", 100),
                safeGetString(rs, "weightUnit", "kg")
            );
            default -> throw new IllegalStateException("Unknown sensor type: " + type);
        };
    }

    /* ---------------- SCHEMA ---------------- */

    @Override
    public String createTableQuery() {
        return """
            CREATE TABLE IF NOT EXISTS Sensor (
                id INTEGER PRIMARY KEY,
                type TEXT NOT NULL,
                location TEXT,
                status TEXT DEFAULT 'inactive',
                startThreshold REAL,
                tolerance REAL,
                targetTemperature REAL,
                temperatureUnit TEXT,
                initialWeight REAL,
                capacity REAL,
                weightUnit TEXT
            );
        """;
    }

    /* ---------------- HELPERS ---------------- */

    private String safeGetString(ResultSet rs, String column, String def) {
        try {
            String val = rs.getString(column);
            return val != null ? val : def;
        } catch (SQLException e) {
            return def;
        }
    }

    private double safeGetDouble(ResultSet rs, String column, double def) {
        try {
            double val = rs.getDouble(column);
            return rs.wasNull() ? def : val;
        } catch (SQLException e) {
            return def;
        }
    }
}
