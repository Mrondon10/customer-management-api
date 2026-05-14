package com.company.customer.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class ErrorHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        if (e instanceof NotFoundException) {
            return error(Response.Status.NOT_FOUND, e.getMessage());
        }
        if (e instanceof ConstraintViolationException cve) {
            String message = cve.getConstraintViolations().stream()
                    .map(v -> {
                        String path = v.getPropertyPath().toString();
                        // strip method/param prefix: create.request.email -> email
                        int dot = path.lastIndexOf('.');
                        return (dot >= 0 ? path.substring(dot + 1) : path) + ": " + v.getMessage();
                    })
                    .collect(Collectors.joining(", "));
            return error(Response.Status.BAD_REQUEST, message);
        }
        if (e instanceof IllegalArgumentException) {
            return error(Response.Status.CONFLICT, e.getMessage());
        }
        return error(Response.Status.INTERNAL_SERVER_ERROR, "Unexpected error");
    }

    private Response error(Response.Status status, String message) {
        return Response.status(status)
                .entity(Map.of("error", message))
                .build();
    }
}
