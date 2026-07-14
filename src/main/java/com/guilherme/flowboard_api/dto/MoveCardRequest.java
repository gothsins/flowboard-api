package com.guilherme.flowboard_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveCardRequest {

    @NotNull(message = "Target column id is required")
    private Long targetColumnId;

    @NotNull(message = "New position is required")
    private Integer newPosition;
}