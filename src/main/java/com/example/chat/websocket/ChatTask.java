package com.example.chat.websocket;

import lombok.Getter;

@Getter
public class ChatTask {
    private final String destination;
    private final Object message;

    public ChatTask(String destination, Object message) {
        this.destination = destination;
        this.message = message;
    }
}
