package com.guilherme.flowboard_api.websocket;

import com.guilherme.flowboard_api.config.KafkaTopics;
import com.guilherme.flowboard_api.dto.ActivityEventMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityEventConsumer {
    private final SimpMessagingTemplate messagingTemplate;
    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @KafkaListener(topics = KafkaTopics.BOARD_ACTIVITY, groupId = "flowboard-activity-group")
    public void consume(ActivityEventMessage event) {
        if (!processedEvents.add(event.getEventId())) {
            log.info("Evento duplicado ignorado: {}", event.getEventId());
            return;
        }
        log.info("Processando evento: {}", event.getEventId());
        if (event.getDetails() != null && event.getDetails().contains("falha-teste")) {
            throw new RuntimeException("Falha simulada para testar retry/DLT");
        }
        messagingTemplate.convertAndSend("/topic/board/" + event.getBoardId(), event);
    }
}