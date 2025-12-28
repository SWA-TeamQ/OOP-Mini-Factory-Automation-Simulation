import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ProcessingStation extends Station {

    private String processType;
    private int defaultProcessingTimeMs;

    private List<Machine> machines;
    private List<Item> inputQueue;
    private List<Item> completedItems;

    private int totalItemsProcessed;
    private LocalDateTime lastActionTime = null;

    // ================== MACHINE ==================
    private class Machine {
        private String supportedProcessType;
        private boolean busy;
        private Item currentItem;
        private int remainingTimeMs;

        public Machine(String supportedProcessType) {
            this.supportedProcessType = supportedProcessType;
            this.busy = false;
        }

        public boolean isFree() {
            return !busy;
        }

        public boolean canProcess(String stationProcessType) {
            return supportedProcessType.equals(stationProcessType)
                    || supportedProcessType.equals("UNIVERSAL");
        }

        public void assignItem(Item item, int processingTimeMs) {
            this.currentItem = item;
            this.remainingTimeMs = processingTimeMs;
            this.busy = true;

            item.setStatus("PROCESSING");
            item.addTrace("ASSIGNED_TO_MACHINE_" + supportedProcessType + "_AT_" + getStationName());

            System.out.println(
                    "Item [" + item.getItemId() + "] assigned to machine [" + supportedProcessType + "]");
        }

        public Item tick(int tickMs) {
            if (!busy)
                return null;

            remainingTimeMs -= tickMs;

            if (remainingTimeMs <= 0) {
                busy = false;
                Item finished = currentItem;
                currentItem = null;

                finished.setStatus("PROCESSED");
                finished.addProcessingStep(supportedProcessType);
                finished.addTrace("COMPLETED_PROCESSING_AT_" + getStationName());

                System.out.println(
                        "Item [" + finished.getItemId() + "] finished on machine [" + supportedProcessType + "]");
                return finished;
            }

            return null;
        }
    }
    // ============================================

    public ProcessingStation(String stationId, String stationName, int capacity,
            String processType, int defaultProcessingTimeMs) {
        super(stationId, stationName, capacity);

        this.processType = processType;
        this.defaultProcessingTimeMs = defaultProcessingTimeMs;

        this.machines = new ArrayList<>();
        this.inputQueue = new ArrayList<>();
        this.completedItems = new ArrayList<>();

        this.totalItemsProcessed = 0;

        // Default setup: 1 specific machine, 1 universal
        addMachine(processType);
        addMachine("UNIVERSAL");
    }

    public void addMachine(String type) {
        machines.add(new Machine(type));
    }

    // ================== CLOCK TICK ==================
    public void onTick(LocalDateTime currentTime) {
        long deltaMs = 1000;
        if (lastActionTime != null) {
            deltaMs = ChronoUnit.MILLIS.between(lastActionTime, currentTime);
        }
        lastActionTime = currentTime;

        tick((int) deltaMs);
    }

    private void tick(int tickMs) {
        if (!getStatus().equals(STATUS_RUNNING))
            return;

        // 1. Update machines
        for (Machine machine : machines) {
            Item finished = machine.tick(tickMs);
            if (finished != null) {
                completedItems.add(finished);
                totalItemsProcessed++;
                setCurrentItemCount(getCurrentItemCount() - 1);
            }
        }

        // 2. Assign waiting items to machines based on item preference
        Iterator<Item> iterator = inputQueue.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            Machine assigned = null;

            // Try preferred machine first
            if (item.getPreferredMachineType() != null) {
                for (Machine machine : machines) {
                    if (machine.isFree() && machine.supportedProcessType.equals(item.getPreferredMachineType())) {
                        assigned = machine;
                        break;
                    }
                }
            }

            // Fallback: any compatible free machine (universal)
            if (assigned == null) {
                for (Machine machine : machines) {
                    if (machine.isFree() && machine.canProcess(processType)) {
                        assigned = machine;
                        break;
                    }
                }
            }

            // Assign item if a machine was found
            if (assigned != null) {
                assigned.assignItem(item, defaultProcessingTimeMs);
                setCurrentItemCount(getCurrentItemCount() + 1);
                iterator.remove();
            }
        }
    }
    // =================================================

    @Override
    public boolean processItem(Item item) {
        if (!getStatus().equals(STATUS_RUNNING)) {
            System.out.println("ProcessingStation is not running.");
            return false;
        }

        inputQueue.add(item);
        item.setStatus("WAITING_FOR_MACHINE");
        item.addTrace("QUEUED_AT_PROCESSING_STATION_" + getStationName());

        System.out.println(
                "Item [" + item.getItemId() + "] queued at " + getStationName());
        return true;
    }

    public List<Item> getCompletedItems() {
        return completedItems;
    }

    public int getTotalItemsProcessed() {
        return totalItemsProcessed;
    }

    @Override
    public String getStationType() {
        return "PROCESSING_STATION";
    }

    @Override
    public void displayStatus() {
        super.displayStatus();
        System.out.println("Process Type: " + processType);
        System.out.println("Machines: " + machines.size());
        System.out.println("Items Waiting: " + inputQueue.size());
        System.out.println("Total Items Processed: " + totalItemsProcessed);
        System.out.println("================================");
    }
}