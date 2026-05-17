package com.company.customer.service;

import com.company.customer.client.CountryResponse;
import com.company.customer.client.RestCountriesClient;
import com.company.customer.dto.CustomerRequest;
import com.company.customer.dto.CustomerResponse;
import com.company.customer.dto.CustomerUpdateRequest;
import com.company.customer.entity.Customer;
import com.company.customer.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class CustomerService {

    final CustomerRepository repository;
    final RestCountriesClient countriesClient;

    public CustomerService(CustomerRepository repository, @RestClient RestCountriesClient countriesClient) {
        this.repository = repository;
        this.countriesClient = countriesClient;
    }

    @Transactional
    public CustomerResponse create(CustomerRequest req) {
        repository.findByEmail(req.email).ifPresent(c -> {
            throw new IllegalArgumentException("El correo ya esta en uso: " + req.email);
        });

        String demonym = getDemonym(req.country.toUpperCase());

        Customer customer = new Customer();
        customer.firstName = req.firstName;
        customer.middleName = req.middleName;
        customer.lastName = req.lastName;
        customer.secondLastName = req.secondLastName;
        customer.email = req.email;
        customer.address = req.address;
        customer.phone = req.phone;
        customer.country = req.country.toUpperCase();
        customer.demonym = demonym;

        repository.persist(customer);
        return CustomerResponse.from(customer);
    }

    public List<CustomerResponse> findAll(String country) {
        List<Customer> customers = (country != null && !country.isBlank())
                ? repository.findByCountry(country)
                : repository.listAll();
        return customers.stream().map(CustomerResponse::from).toList();
    }

    public CustomerResponse findById(Long id) {
        return CustomerResponse.from(getOrThrow(id));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerUpdateRequest req) {
        Customer customer = getOrThrow(id);

        if (!customer.email.equals(req.email)) {
            repository.findByEmail(req.email).ifPresent(c -> {
                throw new IllegalArgumentException("El correo ya esta en uso: " + req.email);
            });
        }

        String pais = req.country.toUpperCase();
        if (!customer.country.equals(pais)) {
            customer.demonym = getDemonym(pais);
            customer.country = pais;
        }

        customer.email = req.email;
        customer.address = req.address;
        customer.phone = req.phone;

        return CustomerResponse.from(customer);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw new NotFoundException("Cliente no encontrado con id: " + id);
        }
    }

    private Customer getOrThrow(Long id) {
        return repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado con id: " + id));
    }

    private String getDemonym(String countryCode) {
        try {
            List<CountryResponse> result = countriesClient.getByCode(countryCode);
            if (result != null && !result.isEmpty()) {
                return result.get(0).getDemonym();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Codigo de pais invalido o desconocido: " + countryCode);
        }
        return null;
    }
}
