package com.guilherme.flowboard_api.dto;

import com.guilherme.flowboard_api.entity.BoardColumn;
import lombok.Getter;

@Getter
public class BoardColumnResponse {

    private final Long id;
    private final String title;
    private final Integer position;
    private final Long boardId;

    public BoardColumnResponse(BoardColumn column) {
        this.id = column.getId();
        this.title = column.getTitle();
        this.position = column.getPosition();
        this.boardId = column.getBoard().getId();
    }
}