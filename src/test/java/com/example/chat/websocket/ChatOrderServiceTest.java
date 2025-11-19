package com.example.chat.websocket;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Duration;

@ExtendWith(MockitoExtension.class)
public class ChatOrderServiceTest {

    @Mock
    SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    ChatOrderService chatOrderService;

    @Test
    void 메시지가_순서대로_처리되는지_테스트() {
        Long roomId = 1L;
        chatOrderService.enqueue(roomId, new ChatTask("/topic/chat/1", "a"));
        chatOrderService.enqueue(roomId, new ChatTask("/topic/chat/1", "b"));
        chatOrderService.enqueue(roomId, new ChatTask("/topic/chat/1", "c"));

        Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    InOrder inOrder = Mockito.inOrder(messagingTemplate);

                    inOrder.verify(messagingTemplate).convertAndSend("/topic/chat/1", "a");
                    inOrder.verify(messagingTemplate).convertAndSend("/topic/chat/1", "b");
                    inOrder.verify(messagingTemplate).convertAndSend("/topic/chat/1", "c");
                });
    }
}
