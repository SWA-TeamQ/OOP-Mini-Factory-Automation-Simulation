package org.automation.ui;

import java.util.Scanner;

import org.automation.controllers.WorkFlowController;
import org.automation.ui.helpers.EventLog;
import org.automation.ui.helpers.FactorySnapshotView;
import org.automation.ui.helpers.StatusView;

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

        while (running) {
            showMenu();
            if (!scanner.hasNextInt()) {
                scanner.nextLine(); // consume invalid input
                continue;
            }
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> controller.startProduction();
                case 2 -> controller.stopProduction();
                case 3 -> snapshotView.displaySnapshot();
                case 4 -> statusView.displayMachineStatus();
                case 5 -> displayEventLog();
                case 6 -> addCustomMachine();
                case 7 -> addCustomProduct();
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
        System.out.println("6. Add Custom Machine");
        System.out.println("7. Add Custom Product");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    private void displayEventLog() {
        System.out.println("=== EVENT LOG ===");
        eventLog.getLogs().forEach(System.out::println);
        System.out.println("=================\n");
    }

    private void addCustomMachine() {
        System.out.print("Enter Machine ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Machine Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Machine Type (INPUT/PROCESSING/PACKAGING): ");
        String type = scanner.nextLine();
        // Station is automatically inferred

        try {
            controller.registerMachine(id, name, type);
            System.out.println("Machine registered successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addCustomProduct() {
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine();
        controller.registerProduct(id);
        System.out.println("Product " + id + " registered.");
    }
}
