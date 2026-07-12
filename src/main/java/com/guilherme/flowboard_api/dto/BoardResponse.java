package com.guilherme.flowboard_api.dto;

import com.guilherme.flowboard_api.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BoardResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final String ownerName;
    private final List<String> memberNames;
    private final LocalDateTime createdAt;

    public BoardResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.description = board.getDescription();
        this.ownerName = board.getOwner().getName();
        this.memberNames = board.getMembers().stream()
                .map(member -> member.getName())
                .toList();
        this.createdAt = board.getCreatedAt();
    }
}