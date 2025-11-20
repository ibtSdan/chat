package com.example.chat.controller;

import com.example.chat.dto.ChatMessageDto;
import com.example.chat.websocket.ChatOrderService;
import com.example.chat.websocket.ChatTask;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatOrderService chatOrderService;

    @MessageMapping("/chat/send")
    public void sendMessage(ChatMessageDto dto){
        ChatTask task = new ChatTask("/topic/chat/"+dto.getRoomId(), dto);
        chatOrderService.enqueue(dto.getRoomId(), task);
    }
}
