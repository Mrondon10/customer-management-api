package com.company.customer.service;

import com.company.customer.client.CountryResponse;
import com.company.customer.client.RestCountriesClient;
import com.company.customer.dto.CustomerRequest;
import com.company.customer.dto.CustomerResponse;
import com.company.customer.dto.CustomerUpdateRequest;
import com.company.customer.entity.Customer;
import com.company.customer.repository.CustomerRepository;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    CustomerRepository repository;

    @Mock
    RestCountriesClient countriesClient;

    CustomerService service;

    @BeforeEach
    void setup() {
        service = new CustomerService(repository, countriesClient);
    }

    private CountryResponse mockCountry(String demonym) {
        CountryResponse cr = new CountryResponse();
        cr.demonyms = Map.of("eng", Map.of("m", demonym));
        return cr;
    }

    private Customer sampleCustomer() {
        Customer c = new Customer();
        c.id = 1L;
        c.firstName = "John";
        c.lastName = "Doe";
        c.email = "john@example.com";
        c.address = "123 Main St";
        c.phone = "555-1234";
        c.country = "US";
        c.demonym = "American";
        return c;
    }

    private CustomerRequest sampleRequest() {
        CustomerRequest req = new CustomerRequest();
        req.firstName = "John";
        req.lastName = "Doe";
        req.email = "john@example.com";
        req.address = "123 Main St";
        req.phone = "555-1234";
        req.country = "US";
        return req;
    }

    // --- create ---

    @Test
    void create_success() {
        when(repository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(countriesClient.getByCode("US")).thenReturn(List.of(mockCountry("American")));
        doNothing().when(repository).persist(any(Customer.class));

        CustomerResponse result = service.create(sampleRequest());

        assertEquals("John", result.firstName);
        assertEquals("American", result.demonym);
        assertEquals("US", result.country);
        verify(repository).persist(any(Customer.class));
    }

    @Test
    void create_duplicateEmail_throwsIllegalArgument() {
        when(repository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleCustomer()));

        assertThrows(IllegalArgumentException.class, () -> service.create(sampleRequest()));
        verify(countriesClient, never()).getByCode(anyString());
    }

    @Test
    void create_invalidCountry_throwsIllegalArgument() {
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(countriesClient.getByCode("XX")).thenThrow(new RuntimeException("Not found"));

        CustomerRequest req = sampleRequest();
        req.country = "XX";

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }

    @Test
    void create_normalizesCountryToUpperCase() {
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(countriesClient.getByCode("US")).thenReturn(List.of(mockCountry("American")));
        doNothing().when(repository).persist(any(Customer.class));

        CustomerRequest req = sampleRequest();
        req.country = "us";

        CustomerResponse result = service.create(req);

        assertEquals("US", result.country);
    }

    // --- findAll ---

    @Test
    void findAll_noFilter_returnsAll() {
        when(repository.listAll()).thenReturn(List.of(sampleCustomer()));

        List<CustomerResponse> result = service.findAll(null);

        assertEquals(1, result.size());
        verify(repository).listAll();
        verify(repository, never()).findByCountry(anyString());
    }

    @Test
    void findAll_withCountry_returnsFiltered() {
        when(repository.findByCountry("US")).thenReturn(List.of(sampleCustomer()));

        List<CustomerResponse> result = service.findAll("US");

        assertEquals(1, result.size());
        assertEquals("US", result.get(0).country);
        verify(repository, never()).listAll();
    }

    // --- findById ---

    @Test
    void findById_existing_returnsCustomer() {
        when(repository.findByIdOptional(1L)).thenReturn(Optional.of(sampleCustomer()));

        CustomerResponse result = service.findById(1L);

        assertEquals(1L, result.id);
        assertEquals("john@example.com", result.email);
    }

    @Test
    void findById_notFound_throwsNotFoundException() {
        when(repository.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(99L));
    }

    // --- update ---

    @Test
    void update_sameCountry_doesNotCallExternalApi() {
        when(repository.findByIdOptional(1L)).thenReturn(Optional.of(sampleCustomer()));
        when(repository.findByEmail("new@example.com")).thenReturn(Optional.empty());

        CustomerUpdateRequest req = new CustomerUpdateRequest();
        req.email = "new@example.com";
        req.address = "New Address";
        req.phone = "555-0000";
        req.country = "US";

        CustomerResponse result = service.update(1L, req);

        assertEquals("new@example.com", result.email);
        assertEquals("American", result.demonym);
        verify(countriesClient, never()).getByCode(anyString());
    }

    @Test
    void update_countryChanged_updatesDemonym() {
        when(repository.findByIdOptional(1L)).thenReturn(Optional.of(sampleCustomer()));
        when(countriesClient.getByCode("CR")).thenReturn(List.of(mockCountry("Costa Rican")));

        CustomerUpdateRequest req = new CustomerUpdateRequest();
        req.email = "john@example.com"; // same email, no duplicate check triggered
        req.address = "San José";
        req.phone = "555-1234";
        req.country = "CR";

        CustomerResponse result = service.update(1L, req);

        assertEquals("CR", result.country);
        assertEquals("Costa Rican", result.demonym);
    }

    @Test
    void update_duplicateEmail_throwsIllegalArgument() {
        Customer existing = sampleCustomer();
        Customer other = sampleCustomer();
        other.id = 2L;
        other.email = "taken@example.com";

        when(repository.findByIdOptional(1L)).thenReturn(Optional.of(existing));
        when(repository.findByEmail("taken@example.com")).thenReturn(Optional.of(other));

        CustomerUpdateRequest req = new CustomerUpdateRequest();
        req.email = "taken@example.com";
        req.address = "addr";
        req.phone = "123";
        req.country = "US";

        assertThrows(IllegalArgumentException.class, () -> service.update(1L, req));
    }

    @Test
    void update_notFound_throwsNotFoundException() {
        when(repository.findByIdOptional(99L)).thenReturn(Optional.empty());

        CustomerUpdateRequest req = new CustomerUpdateRequest();
        req.email = "x@x.com";
        req.address = "addr";
        req.phone = "123";
        req.country = "US";

        assertThrows(NotFoundException.class, () -> service.update(99L, req));
    }

    // --- delete ---

    @Test
    void delete_existing_success() {
        when(repository.deleteById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> service.delete(1L));
    }

    @Test
    void delete_notFound_throwsNotFoundException() {
        when(repository.deleteById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.delete(99L));
    }
}
