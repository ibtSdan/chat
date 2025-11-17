package com.example.chat.controller;

import com.example.chat.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send")
    public void sendMessage(ChatMessageDto dto){
        String destination = "/topic/chat/"+dto.getRoomId();
        messagingTemplate.convertAndSend(destination, dto);
    }
}
