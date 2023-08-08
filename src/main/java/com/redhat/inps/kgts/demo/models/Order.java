package com.redhat.inps.kgts.demo.models;

public class Order {
    public long id;
    public String order_date;
    public int purchaser;
    public int quantity;
    public int product_id;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", order_date='" + order_date + '\'' +
                ", purchaser=" + purchaser +
                ", quantity=" + quantity +
                ", product_id=" + product_id +
                '}';
    }
}
