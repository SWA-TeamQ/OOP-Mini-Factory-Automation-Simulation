import java.util.ArrayList;
import java.util.List;

public class ActuatorManager {

    private final List<Actuator> actuators = new ArrayList<>();

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
        else System.out.println("[ActuatorManager] actuator not found: " + id);
    }

    public void deactivateActuator(String id) {
        Actuator a = getActuator(id);
        if (a != null) a.deactivate();
        else System.out.println("[ActuatorManager] actuator not found: " + id);
    }

    public void listActuators() {
        for (Actuator a : actuators) {
            System.out.println(a.getId() + " → Active: " + a.getStatus());
        }
    }

    public List<Actuator> getAll() {
        return actuators;
    }
}
