import java.util.ArrayList;
import java.util.List;

public class PackagingStation extends Station {

    private String packagingType;
    private int itemsPerPackage;
    private List<Item> itemsToPackage;
    private List<Package> completedPackages;
    private int totalPackagesCreated;
    private String labelPrefix;

    public PackagingStation(String stationId, String stationName, int capacity,
            String packagingType, int itemsPerPackage) {
        super(stationId, stationName, capacity);
        this.packagingType = packagingType;
        this.itemsPerPackage = itemsPerPackage;
        this.itemsToPackage = new ArrayList<>();
        this.completedPackages = new ArrayList<>();
        this.totalPackagesCreated = 0;
        this.labelPrefix = "PKG";
    }

    public String getPackagingType() {
        return packagingType;
    }

    public void setPackagingType(String packagingType) {
        this.packagingType = packagingType;
    }

    public int getItemsPerPackage() {
        return itemsPerPackage;
    }

    public void setItemsPerPackage(int itemsPerPackage) {
        this.itemsPerPackage = itemsPerPackage;
    }

    public int getTotalPackagesCreated() {
        return totalPackagesCreated;
    }

    public String getLabelPrefix() {
        return labelPrefix;
    }

    public void setLabelPrefix(String labelPrefix) {
        this.labelPrefix = labelPrefix;
    }

    public List<Item> getItemsToPackage() {
        return itemsToPackage;
    }

    public List<Package> getCompletedPackages() {
        return completedPackages;
    }

    public boolean addItemForPackaging(Item item) {
        if (!hasCapacity()) {
            System.out.println("PackagingStation is at full capacity.");
            setStatus(STATUS_BLOCKED);
            return false;
        }

        itemsToPackage.add(item);
        setCurrentItemCount(getCurrentItemCount() + 1);
        item.setStatus("READY_FOR_PACKAGING");
        item.addTrace("ENTERED_PACKAGING_STATION_" + getStationName());

        System.out.println("Item [" + item.getItemId() + "] added to packaging queue at " + getStationName());

        if (itemsToPackage.size() >= itemsPerPackage) {
            System.out.println("Enough items collected. Ready to create package.");
        }

        return true;
    }

    public Package createPackage() {
        if (itemsToPackage.size() < itemsPerPackage) {
            System.out.println("Not enough items to create a package. Need " +
                    itemsPerPackage + ", have " + itemsToPackage.size());
            return null;
        }

        if (!getStatus().equals(STATUS_RUNNING)) {
            System.out.println("Station is not running.");
            return null;
        }

        totalPackagesCreated++;
        String packageId = labelPrefix + "-" + String.format("%05d", totalPackagesCreated);
        Package newPackage = new Package(packageId, packagingType);

        for (int i = 0; i < itemsPerPackage; i++) {
            Item item = itemsToPackage.remove(0);
            setCurrentItemCount(getCurrentItemCount() - 1);
            item.setStatus("PACKAGED");
            item.addTrace("PACKAGED_IN_" + packageId);
            newPackage.addItem(item);
        }

        newPackage.seal();
        completedPackages.add(newPackage);

        System.out.println("Package [" + packageId + "] created with " + itemsPerPackage + " items.");
        return newPackage;
    }

    public Package shipPackage() {
        if (completedPackages.isEmpty()) {
            System.out.println("No packages ready for shipping.");
            return null;
        }

        Package packageToShip = completedPackages.remove(0);
        packageToShip.markAsShipped();
        System.out.println("Package [" + packageToShip.getPackageId() + "] shipped.");
        for (Item item : packageToShip.getItems()) {
            item.addTrace("SHIPPED_IN_" + packageToShip.getPackageId());
        }
        return packageToShip;
    }

    @Override
    public boolean processItem(Item item) {
        if (!getStatus().equals(STATUS_RUNNING)) {
            System.out.println("PackagingStation is not running. Start the station first.");
            return false;
        }

        if (!addItemForPackaging(item)) {
            return false;
        }

        if (itemsToPackage.size() >= itemsPerPackage) {
            createPackage();
        }

        return true;
    }

    @Override
    public String getStationType() {
        return "PACKAGING_STATION";
    }

    @Override
    public void displayStatus() {
        super.displayStatus();
        System.out.println("Packaging Type: " + packagingType);
        System.out.println("Items Per Package: " + itemsPerPackage);
        System.out.println("Items Waiting: " + itemsToPackage.size());
        System.out.println("Packages Ready: " + completedPackages.size());
        System.out.println("Total Packages Created: " + totalPackagesCreated);
        System.out.println("================================");
    }

    public static class Package {
        private String packageId;
        private String packageType;
        private List<Item> items;
        private boolean isSealed;
        private boolean isShipped;
        private long createdTimestamp;

        public Package(String packageId, String packageType) {
            this.packageId = packageId;
            this.packageType = packageType;
            this.items = new ArrayList<>();
            this.isSealed = false;
            this.isShipped = false;
            this.createdTimestamp = System.currentTimeMillis();
        }

        public String getPackageId() {
            return packageId;
        }

        public String getPackageType() {
            return packageType;
        }

        public List<Item> getItems() {
            return items;
        }

        public boolean isSealed() {
            return isSealed;
        }

        public boolean isShipped() {
            return isShipped;
        }

        public void addItem(Item item) {
            if (!isSealed) {
                items.add(item);
            }
        }

        public void seal() {
            this.isSealed = true;
            System.out.println("Package [" + packageId + "] sealed.");
        }

        public void markAsShipped() {
            this.isShipped = true;
        }

        @Override
        public String toString() {
            return "Package[" + packageId + "] Type: " + packageType +
                    " | Items: " + items.size() +
                    " | Sealed: " + isSealed +
                    " | Shipped: " + isShipped;
        }
    }
}
