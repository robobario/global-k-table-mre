package com.redhat.inps.kgts.demo.models;

public class OrderWithCustomer {
    public Order order;
    public Customer customer;

    public OrderWithCustomer(Order order, Customer customer) {
        this.order = order;
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "OrderWithCustomer{" +
                "order=" + order +
                ", customer=" + customer +
                '}';
    }
}
