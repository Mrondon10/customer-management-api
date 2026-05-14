package com.company.customer.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer extends PanacheEntity {

    @Column(name = "first_name", nullable = false)
    public String firstName;

    @Column(name = "middle_name")
    public String middleName;

    @Column(name = "last_name", nullable = false)
    public String lastName;

    @Column(name = "second_last_name")
    public String secondLastName;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(nullable = false)
    public String address;

    @Column(nullable = false)
    public String phone;

    @Column(nullable = false, length = 2)
    public String country;

    @Column
    public String demonym;
}
