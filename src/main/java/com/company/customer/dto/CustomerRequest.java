package com.company.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerRequest {

    @NotBlank(message = "First name is required")
    public String firstName;

    public String middleName;

    @NotBlank(message = "Last name is required")
    public String lastName;

    public String secondLastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    public String email;

    @NotBlank(message = "Address is required")
    public String address;

    @NotBlank(message = "Phone is required")
    public String phone;

    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 2, message = "Country must be a 2-character ISO 3166 code")
    @Pattern(regexp = "[A-Za-z]{2}", message = "Country must contain only letters")
    public String country;
}
