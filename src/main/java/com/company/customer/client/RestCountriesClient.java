package com.company.customer.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "rest-countries")
@Path("/v3.1")
public interface RestCountriesClient {

    @GET
    @Path("/alpha/{code}")
    List<CountryResponse> getByCode(@PathParam("code") String code);
}
