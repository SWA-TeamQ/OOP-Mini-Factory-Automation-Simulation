public class PackagingMachine extends Actuator {

    public PackagingMachine(String id) {
        super(id);
    }

    @Override
    public void activate() {
        super.activate();
        System.out.println("PackagingMachine " + id + " is ready for packaging.");
    }

    @Override
    public void deactivate() {
        super.deactivate();
        System.out.println("PackagingMachine " + id + " stopped.");
    }

    public void packageItem(ProductItem item) {
        if (!isActive) {
            System.out.println("Cannot package. Machine is OFF.");
            return;
        }

        System.out.println("Packaging item: " + item.getName());
        
    }
}
