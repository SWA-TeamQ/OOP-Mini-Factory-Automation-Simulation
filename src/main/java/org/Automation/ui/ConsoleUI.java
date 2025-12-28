package org.Automation.ui;

import org.Automation.Controllers.WorkFlowController;
import org.Automation.entities.ProductItem;
import org.Automation.ui.helpers.EventLog;
import org.Automation.ui.helpers.FactorySnapshotView;
import org.Automation.ui.helpers.StatusView;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final WorkFlowController controller;
    private final Scanner scanner;
    private final EventLog eventLog;
    private final FactorySnapshotView snapshotView;
    private final StatusView statusView;

    public ConsoleUI(WorkFlowController controller,
                     EventLog eventLog,
                     FactorySnapshotView snapshotView,
                     StatusView statusView) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
        this.eventLog = eventLog;
        this.snapshotView = snapshotView;
        this.statusView = statusView;
    }

   public void start() {
    boolean running = true;
// List<ProductItem> products = List.of(
//     new ProductItem("ITEM-001"),
//     new ProductItem("ITEM-002")
// );



    while (running) {
        showMenu();
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1 -> {
                controller.startProduction();
                // In the simulation loop:
                if (controller.isProductionRunning()) {
                    controller.runProductionStep(); // one tick per second
                }
                }
            case 2 -> controller.stopProduction();
            case 3 -> snapshotView.displaySnapshot();
            case 4 -> statusView.displayMachineStatus();
            case 5 -> displayEventLog();
            case 6 -> {
    System.out.print("Enter product ID to add: ");
    String id = scanner.nextLine();
    controller.addProductItemFromUser(id);
    System.out.println("Product item added: " + id);
}

            case 0 -> {
                controller.stopProduction();
                System.out.println("Exiting simulation...");
                running = false;
                System.exit(0);
            }
            default -> System.out.println("Invalid choice. Try again.");
        }

        // Run one simulation tick AFTER user action
        if (controller.isProductionRunning()) {
            controller.runProductionStep();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}


private void showMenu() {
    System.out.println();
    System.out.println("=== FACTORY SIMULATION MENU ===");
    System.out.println("1. Start Production");
    System.out.println("2. Stop Production");
    System.out.println("3. View Factory Snapshot");
    System.out.println("4. View Machine Status");
    System.out.println("5. View Event Log");
    System.out.println("6. Add Product Item");
    System.out.println("0. Exit");
    System.out.print("Select an option: ");
}



    private void displayEventLog() {
        System.out.println("=== EVENT LOG ===");
        eventLog.getLogs().forEach(System.out::println);
        System.out.println("=================\n");
    }
}
