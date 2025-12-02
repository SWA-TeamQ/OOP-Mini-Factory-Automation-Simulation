import java.time.LocalDateTime;

public class PackagingMachine extends Actuator {

    public PackagingMachine(String id) {
        super(id);
    }

    @Override
    public void activate() {
        super.activate();
        System.out.println("Packaging machine " + id + " ready.");
    }

    @Override
    public void deactivate() {
        super.deactivate();
        System.out.println("Packaging machine " + id + " stopped.");
    }

    @Override
    public void onTick(LocalDateTime currentTime) {
        if (isActive) {
            if (lastActionTime == null ||
                currentTime.minusSeconds(3).isAfter(lastActionTime)) {

                System.out.println("[" + id + "] Packaging items...");
                lastActionTime = currentTime;

                //the next logic will goes here ....
            }
        }
    }
}
