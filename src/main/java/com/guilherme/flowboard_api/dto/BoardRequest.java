package com.guilherme.flowboard_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
}