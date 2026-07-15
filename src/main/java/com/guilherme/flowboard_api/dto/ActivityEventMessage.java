package com.guilherme.flowboard_api.dto;

import com.guilherme.flowboard_api.entity.ActivityLog;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ActivityEventMessage {

    private final Long boardId;
    private final String userName;
    private final String action;
    private final String details;
    private final LocalDateTime timestamp;

    public ActivityEventMessage(ActivityLog log) {
        this.boardId = log.getBoard().getId();
        this.userName = log.getUser().getName();
        this.action = log.getAction().name();
        this.details = log.getDetails();
        this.timestamp = log.getCreatedAt();
    }
}