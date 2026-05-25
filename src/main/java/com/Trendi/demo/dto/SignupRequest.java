package com.Trendi.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SignupRequest {

    @NotBlank
    private String name;

    @Email
    private String email;

    private String password;
}
