package com.company.customer.resource;

import com.company.customer.dto.CustomerRequest;
import com.company.customer.dto.CustomerResponse;
import com.company.customer.dto.CustomerUpdateRequest;
import com.company.customer.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    final CustomerService service;

    public CustomerResource(CustomerService service) {
        this.service = service;
    }

    @POST
    public Response create(@Valid CustomerRequest request) {
        CustomerResponse response = service.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    public List<CustomerResponse> findAll(@QueryParam("country") String country) {
        return service.findAll(country);
    }

    @GET
    @Path("/{id}")
    public CustomerResponse findById(@PathParam("id") Long id) {
        return service.findById(id);
    }

    @PATCH
    @Path("/{id}")
    public CustomerResponse update(@PathParam("id") Long id, @Valid CustomerUpdateRequest request) {
        return service.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
