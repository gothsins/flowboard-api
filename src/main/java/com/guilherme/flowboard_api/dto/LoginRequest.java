package com.guilherme.flowboard_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequest {

    @NotBlank
    private String email;

    @NotBlank
    @ToString.Exclude
    private String password;
}