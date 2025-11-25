package org.Automation.entities;

public class Product {
    public int id;
    public String name;
    public String status;

    public Product(int id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name=" + name + ", status=" + status + "}";
    }
}
