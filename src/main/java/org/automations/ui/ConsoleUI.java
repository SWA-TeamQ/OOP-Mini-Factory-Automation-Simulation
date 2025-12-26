package org.automations.ui;

import java.util.*;

public class ConsoleUI {
  private final Scanner scanner;

  public ConsoleUI() {
    this.scanner = new Scanner(System.in);
  }

  // Low-Level Menu print lines
  public void printHeader(String title) {
    System.out.println("\n==================================================");
    System.out.println("  " + title.toUpperCase());
    System.out.println("==================================================");
  }

  public void printMenu(ArrayList<String> options) {
    System.out.println("\nPlease select an option:");
    for (int i = 0; i < options.size(); i++) {
      System.out.println(" [" + (i + 1) + "] " + options.get(i));
    }
    System.out.print("\nSelection > ");
  }

  public String getUserInput() {
    if (scanner.hasNextLine()) {
      return scanner.nextLine().trim();
    }
    return "";
  }

  public void printStatus(String message) {
    System.out.println(" >> [STATUS]: " + message);
  }

  public void printError(String message) {
    System.err.println(" !! [ERROR]: " + message);
    // Small delay to ensure the error message (System.err) prints
    // before the next menu (System.out) due to stream differences.
    try {
      Thread.sleep(50);
    } catch (InterruptedException ignored) {
    }
  }
}
