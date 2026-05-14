package com.company.customer.repository;

import com.company.customer.entity.Customer;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer> {

    public List<Customer> findByCountry(String country) {
        return list("country", country.toUpperCase());
    }

    public Optional<Customer> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
}
