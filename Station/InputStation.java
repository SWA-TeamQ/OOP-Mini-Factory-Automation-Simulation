import java.util.LinkedList;
import java.util.Queue;

public class InputStation extends Station {

    private Queue<Item> inputQueue;
    private String inputSource;
    private int totalItemsReceived;

    public InputStation(String stationId, String stationName, int capacity, String inputSource) {
        super(stationId, stationName, capacity);
        this.inputQueue = new LinkedList<>();
        this.inputSource = inputSource;
        this.totalItemsReceived = 0;
    }

    public String getInputSource() {
        return inputSource;
    }

    public void setInputSource(String inputSource) {
        this.inputSource = inputSource;
    }

    public int getTotalItemsReceived() {
        return totalItemsReceived;
    }

    public Queue<Item> getInputQueue() {
        return inputQueue;
    }

    public boolean receiveItem(Item item) {
        if (!hasCapacity()) {
            System.out.println("InputStation " + getStationName() + " is at full capacity. Cannot receive more items.");
            setStatus(STATUS_BLOCKED);
            return false;
        }

        inputQueue.add(item);
        setCurrentItemCount(getCurrentItemCount() + 1);
        totalItemsReceived++;
        item.setStatus("RECEIVED");
        item.addTrace("RECEIVED_AT_INPUT_STATION_" + getStationName());

        System.out.println("Item [" + item.getItemId() + "] received at " + getStationName() + " from " + inputSource);
        return true;
    }

    public Item releaseItem() {
        if (inputQueue.isEmpty()) {
            System.out.println("No items in queue to release.");
            return null;
        }

        Item item = inputQueue.poll();
        setCurrentItemCount(getCurrentItemCount() - 1);
        item.setStatus("IN_TRANSIT");
        item.addTrace("RELEASED_FROM_INPUT_STATION_" + getStationName());

        System.out.println("Item [" + item.getItemId() + "] released from " + getStationName());

        if (inputQueue.isEmpty() && getStatus().equals(STATUS_RUNNING)) {
            setStatus(STATUS_IDLE);
        }
        return item;
    }

    @Override
    public boolean processItem(Item item) {
        if (!getStatus().equals(STATUS_RUNNING)) {
            System.out.println("InputStation is not running. Start the station first.");
            return false;
        }

        return receiveItem(item);
    }

    @Override
    public String getStationType() {
        return "INPUT_STATION";
    }

    @Override
    public void displayStatus() {
        super.displayStatus();
        System.out.println("Input Source: " + inputSource);
        System.out.println("Items in Queue: " + inputQueue.size());
        System.out.println("Total Items Received: " + totalItemsReceived);
    }
}
