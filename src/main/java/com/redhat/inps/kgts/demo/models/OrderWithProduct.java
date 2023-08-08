package com.redhat.inps.kgts.demo.models;

public class OrderWithProduct {
    public Order order;
    public Product product;
    public Customer customer;

    public OrderWithProduct() {

    }

    public OrderWithProduct(Order order, Product product) {
        this.order = order;
        this.product = product;
    }

    public OrderWithProduct setCustomer(Customer customer){
        this.customer = customer;
        return this;
    }

    @Override
    public String toString() {
        return "OrderWithProduct{" +
                "order=" + order +
                ", product=" + product +
                ", customer=" + customer +
                '}';
    }
}
