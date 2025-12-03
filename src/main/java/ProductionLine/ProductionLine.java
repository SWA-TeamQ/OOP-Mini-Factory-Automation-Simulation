package ProductionLine;


public class ProductionLine {

    protected final String lineName;
    private boolean running = false;

    public ProductionLine(String lineName) {
        this.lineName = lineName;
    }

    public void startLine() {
        running = true;
        System.out.println("[ProductionLine] " + lineName + " started.");
    }

    public void stopLine() {
        running = false;
        System.out.println("[ProductionLine] " + lineName + " stopped.");
    }

    public void runStep() {
        if (!running) {
            System.out.println("[ProductionLine] " + lineName + " runStep called while stopped.");
            return;
        }
        System.out.println("[ProductionLine] " + lineName + " performing a production step.");
    }

    public boolean isRunning() {
        return running;
    }

    public String getLineName() {
        return lineName;
    }
}
