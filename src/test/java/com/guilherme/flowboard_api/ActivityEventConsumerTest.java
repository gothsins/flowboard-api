package com.guilherme.flowboard_api;

import com.guilherme.flowboard_api.dto.ActivityEventMessage;
import com.guilherme.flowboard_api.websocket.ActivityEventConsumer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ActivityEventConsumerTest {

    @Test
    void deveLancarExceptionQuandoDetailsContemFalhaTeste() {
        SimpMessagingTemplate mockTemplate = Mockito.mock(SimpMessagingTemplate.class);
        ActivityEventConsumer consumer = new ActivityEventConsumer(mockTemplate);

        ActivityEventMessage event = new ActivityEventMessage();
        event.setEventId("teste-falha-1");
        event.setBoardId(1L);
        event.setDetails("falha-teste");

        assertThrows(RuntimeException.class, () -> consumer.consume(event));
    }
}