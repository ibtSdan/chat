package com.example.chat.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class ChatOrderService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ConcurrentMap<Long, BlockingQueue<ChatTask>> roomQueues = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, AtomicBoolean> workerRunning = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void enqueue(Long roomId, ChatTask task) {
        BlockingQueue<ChatTask> q = roomQueues.computeIfAbsent(roomId, id -> new LinkedBlockingQueue<>());
        q.add(task);

        workerRunning.computeIfAbsent(roomId, id -> new AtomicBoolean(false));
        startWorkerIfNeeded(roomId, q);
    }

    private void startWorkerIfNeeded(Long roomId, BlockingQueue<ChatTask> q) {
        AtomicBoolean running = workerRunning.get(roomId);
        if (running.compareAndSet(false, true)) {
            executorService.submit(() -> {
                try {
                    while (true) {
                        ChatTask task = q.take();
                        messagingTemplate.convertAndSend(task.getDestination(), task.getMessage());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    running.set(false);
                }
            });
        }
    }
}
