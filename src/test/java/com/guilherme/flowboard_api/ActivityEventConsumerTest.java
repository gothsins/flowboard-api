package com.guilherme.flowboard_api;

import com.guilherme.flowboard_api.dto.ActivityEventMessage;
import com.guilherme.flowboard_api.websocket.ActivityEventConsumer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

    @Test
    void devePassarSemErroQuandoDetailsNaoContemFalhaTeste() {
        SimpMessagingTemplate mockTemplate = Mockito.mock(SimpMessagingTemplate.class);
        ActivityEventConsumer consumer = new ActivityEventConsumer(mockTemplate);

        ActivityEventMessage event = new ActivityEventMessage();
        event.setEventId("teste-normal-1");
        event.setBoardId(1L);
        event.setDetails("card movido");

        consumer.consume(event);

        verify(mockTemplate, times(1))
                .convertAndSend(eq("/topic/board/1"), eq(event));
    }

    @Test
    void naoDeveProcessarEventoDuplicado() {
        SimpMessagingTemplate mockTemplate = Mockito.mock(SimpMessagingTemplate.class);
        ActivityEventConsumer consumer = new ActivityEventConsumer(mockTemplate);

        ActivityEventMessage event = new ActivityEventMessage();
        event.setEventId("evento-repetido");
        event.setBoardId(1L);
        event.setDetails("card criado");

        consumer.consume(event);
        consumer.consume(event);

        verify(mockTemplate, times(1))
                .convertAndSend(eq("/topic/board/1"), eq(event));
    }

    @Test
    void deveTentarTresVezesAntesDeDesistir() {
        SimpMessagingTemplate mockTemplate = Mockito.mock(SimpMessagingTemplate.class);
        ActivityEventConsumer consumer = new ActivityEventConsumer(mockTemplate);

        ActivityEventMessage event = new ActivityEventMessage();
        event.setEventId("teste-retry-count");
        event.setBoardId(1L);
        event.setDetails("falha-teste");

        int tentativas = 0;
        for (int i = 0; i < 3; i++) {
            try {
                consumer.consume(event);
            } catch (RuntimeException e) {
                tentativas++;
            }
        }

        assertEquals(3, tentativas);
    }
}