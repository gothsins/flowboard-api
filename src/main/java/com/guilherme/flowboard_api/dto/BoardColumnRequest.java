package com.guilherme.flowboard_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardColumnRequest {

    @NotBlank(message = "Title is required")
    private String title;
}