package com.redhat.inps.kgts.demo.models;

public class Customer {
    public long id;
    public String first_name;
    public String last_name;
    public String email;

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
