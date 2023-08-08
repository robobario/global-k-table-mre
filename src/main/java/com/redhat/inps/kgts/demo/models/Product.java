package com.redhat.inps.kgts.demo.models;

public class Product {
    public int id;
    public String name;
    public String description;
    public float weight;

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", weight=" + weight +
                '}';
    }
}
