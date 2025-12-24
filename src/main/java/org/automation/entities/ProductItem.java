package org.automation.entities;

public class ProductItem {
    public int id;
    public String name;
    public double weight;
    public String status;
    public int currentStationId;
    public String createdAt;

    public ProductItem(int id, String name, double weight, String status, String createdAt, int currentStationId) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.status = status;
        this.createdAt = createdAt;
        this.currentStationId = currentStationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getCurrentStationId() {
        return currentStationId;
    }

    public void setCurrentStationId(int currentStationId) {
        this.currentStationId = currentStationId;
    }

    @Override
    public String toString() {
        return "ProductItem{id=" + id + ", name=" + name + ", weight=" + weight + ", status=" + status + ", createdAt="
                + createdAt + ", currentStationId=" + currentStationId + "}";
    }
}

