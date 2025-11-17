package com.example.chat.controller;

import com.example.chat.dto.ChatMessageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatController controller;

    @Test
    void 메시지_보내기_성공(){
        // given
        ChatMessageDto messageDto = new ChatMessageDto();
        messageDto.setContent("안녕");
        messageDto.setSender("A");
        messageDto.setRoomId(1L);

        // when
        controller.sendMessage(messageDto);

        // then
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), (Object) any());
    }

    @Test
    void 메시지_보내기_실패(){
        // given
        ChatMessageDto messageDto = null;

        // when
        try {
            controller.sendMessage(messageDto);
        } catch (Exception ignored) {}

        // then
        verify(messagingTemplate, never()).convertAndSend(anyString(), (Object) any());
    }
}
