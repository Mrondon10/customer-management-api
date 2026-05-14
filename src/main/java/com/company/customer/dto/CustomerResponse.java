package com.company.customer.dto;

import com.company.customer.entity.Customer;

public class CustomerResponse {

    public Long id;
    public String firstName;
    public String middleName;
    public String lastName;
    public String secondLastName;
    public String email;
    public String address;
    public String phone;
    public String country;
    public String demonym;

    public static CustomerResponse from(Customer c) {
        CustomerResponse r = new CustomerResponse();
        r.id = c.id;
        r.firstName = c.firstName;
        r.middleName = c.middleName;
        r.lastName = c.lastName;
        r.secondLastName = c.secondLastName;
        r.email = c.email;
        r.address = c.address;
        r.phone = c.phone;
        r.country = c.country;
        r.demonym = c.demonym;
        return r;
    }
}
