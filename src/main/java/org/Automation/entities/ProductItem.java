package org.Automation.entities;

import java.util.ArrayList;
import java.util.List;

public class ProductItem {

    private final String id;
    private boolean completed;
    private final List<String> history = new ArrayList<>();

    public ProductItem(String id) { this.id = id; }
    public String getId() { return id; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void addHistory(String entry) { history.add(entry); }
    public List<String> getHistory() { return history; }
}
