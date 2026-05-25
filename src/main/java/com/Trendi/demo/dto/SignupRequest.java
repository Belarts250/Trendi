package com.Trendi.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class SignupRequest {

    @NotBlank
    private String name;

    private String email;

    private String password;
}
