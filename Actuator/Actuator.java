import java.time.LocalDateTime;

public class Actuator implements SimulationClock.ClockObserver { // simulation clock to trigger the clock signal

    protected String id;
    protected boolean isActive;
    protected LocalDateTime lastActionTime;

    public Actuator(String id) {
        this.id = id;
        this.isActive = false;

        // register for clock updates
        SimulationClock.getInstance().register(this);
    }

    public void activate() {
        isActive = true;
        System.out.println(id + " activated.");
    }

    public void deactivate() {
        isActive = false;
        System.out.println(id + " deactivated.");
    }

    public boolean getStatus() {
        return isActive;
    }

    public String getId() {
        return id;
    }

    @Override
    public void onTick(LocalDateTime currentTime) {
        
    }
}
