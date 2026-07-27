package com.guilherme.flowboard_api.dto;

import com.guilherme.flowboard_api.entity.ActivityLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActivityEventMessage {

    private Long boardId;
    private String userName;
    private String action;
    private String details;
    private String timestamp;

    public ActivityEventMessage(ActivityLog log) {
        this.boardId = log.getBoard().getId();
        this.userName = log.getUser().getName();
        this.action = log.getAction().name();
        this.details = log.getDetails();
        this.timestamp = log.getCreatedAt().toString();
    }
}