package com.example.chat.dto;

import lombok.Data;

@Data

public class ChatMessageDto {
    private String sender;
    private String content;
    private Long roomId;
}
