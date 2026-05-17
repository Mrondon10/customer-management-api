package com.company.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerRequest {

    @NotBlank(message = "El primer nombre es requerido")
    public String firstName;

    public String middleName;

    @NotBlank(message = "El primer apellido es requerido")
    public String lastName;

    public String secondLastName;

    @NotBlank(message = "El correo es requerido")
    @Email(message = "El correo no tiene un formato valido")
    public String email;

    @NotBlank(message = "La direccion es requerida")
    public String address;

    @NotBlank(message = "el telefono es requerido")
    public String phone;

    @NotBlank(message = "El pais es requerido")
    @Size(min = 2, max = 2, message = "El pais debe ser un codigo ISO 3166 de 2 letras")
    @Pattern(regexp = "[A-Za-z]{2}", message = "El pais debe contener solo letras")
    public String country;
}
