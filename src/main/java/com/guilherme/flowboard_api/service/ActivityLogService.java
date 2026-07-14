package com.guilherme.flowboard_api.service;

import com.guilherme.flowboard_api.entity.ActivityLog;
import com.guilherme.flowboard_api.entity.Board;
import com.guilherme.flowboard_api.entity.User;
import com.guilherme.flowboard_api.repository.ActivityLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public void log(Board board, User user, ActivityLog.ActionType action, String details) {
        ActivityLog entry = ActivityLog.builder()
                .board(board)
                .user(user)
                .action(action)
                .details(details)
                .build();

        activityLogRepository.save(entry);
    }
}