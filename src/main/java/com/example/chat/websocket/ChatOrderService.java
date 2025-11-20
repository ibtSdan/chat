package com.example.chat.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
public class ChatOrderService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ConcurrentMap<Long, RoomState> roomStates = new ConcurrentHashMap<>();
    private final ExecutorService workerPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    private static final long IDLE_TIMEOUT_MS = 10_000;

    public void enqueue(Long roomId, ChatTask task) {
        RoomState state = roomStates.computeIfAbsent(roomId, id -> new RoomState());
        state.getQueue().add(task);
        startWorkerIfNeeded(roomId, state);
    }

    private void startWorkerIfNeeded(Long roomId, RoomState state) {
        if (state.getWorkerRunning().compareAndSet(false, true)) {
            workerPool.submit(() -> runWorker(roomId, state));
        }
    }

    private void runWorker(Long roomId, RoomState state) {
        BlockingQueue<ChatTask> queue = state.getQueue();

        try {
            while (true) {
                ChatTask task = queue.poll();
                if (task != null) {
                    messagingTemplate.convertAndSend(task.getDestination(), task.getMessage());
                } else {
                    Thread.sleep(IDLE_TIMEOUT_MS);
                    if (queue.isEmpty()) {
                        state.getWorkerRunning().set(false);
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            state.getWorkerRunning().set(false);
        }
    }
}
