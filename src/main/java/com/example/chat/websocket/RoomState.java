package com.example.chat.websocket;

import lombok.Getter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class RoomState {
    private final BlockingQueue<ChatTask> queue = new LinkedBlockingQueue<>();
    private final AtomicBoolean workerRunning = new AtomicBoolean(false);
}
