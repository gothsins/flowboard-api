package com.guilherme.flowboard_api.websocket;

import com.guilherme.flowboard_api.config.KafkaTopics;
import com.guilherme.flowboard_api.dto.ActivityEventMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityEventConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = KafkaTopics.BOARD_ACTIVITY, groupId = "flowboard-api-#{T(java.util.UUID).randomUUID()}")
    public void consume(ActivityEventMessage event) {
        messagingTemplate.convertAndSend("/topic/board/" + event.getBoardId(), event);
    }
}