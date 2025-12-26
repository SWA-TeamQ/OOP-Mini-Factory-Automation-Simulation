package org.automations.entities;

// WARNING: This class used to be abstract. It had to be changed to a concrete class
// for invocation of SensorRepository.mapRow() method. at line 15.
public class Sensor {
  public int id;
  public String type;
  public int machineId;
  public String status;

  public Sensor(int id, String type, int machineId, String status) {
    this.id = id;
    this.type = type;
    this.machineId = machineId;
    this.status = status;
  }

  public String getId() {
    return String.valueOf(id);
  }

  @Override
  public String toString() {
    return "Sensor{id=" + id + ", type=" + type + ", machineId=" + machineId + ", status=" + status + "}";
  }
}
