package com.guilherme.flowboard_api.dto;

import com.guilherme.flowboard_api.entity.Card;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CardResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final Integer position;
    private final Long boardColumnId;
    private final String assigneeName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public CardResponse(Card card) {
        this.id = card.getId();
        this.title = card.getTitle();
        this.description = card.getDescription();
        this.position = card.getPosition();
        this.boardColumnId = card.getBoardColumn().getId();
        this.assigneeName = card.getAssignee() != null ? card.getAssignee().getName() : null;
        this.createdAt = card.getCreatedAt();
        this.updatedAt = card.getUpdatedAt();
    }
}