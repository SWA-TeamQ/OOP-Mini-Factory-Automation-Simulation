import java.util.ArrayList;

public class ActuatorManager {

    private ArrayList<Actuator> actuators = new ArrayList<>();

    public void addActuator(Actuator actuator) {
        actuators.add(actuator);
    }

    public Actuator getActuator(String id) {
        for (Actuator a : actuators) {
            if (a.getId().equals(id)) {
                return a;
            }
        }
        return null;
    }

    public void activateActuator(String id) {
        Actuator a = getActuator(id);
        if (a != null) a.activate();
    }

    public void deactivateActuator(String id) {
        Actuator a = getActuator(id);
        if (a != null) a.deactivate();
    }

    public void listActuators() {
        for (Actuator a : actuators) {
            System.out.println(a.getId() + " → Active: " + a.getStatus());
        }
    }
}
