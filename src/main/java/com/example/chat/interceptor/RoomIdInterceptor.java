package com.example.chat.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j

public class RoomIdInterceptor implements HandshakeInterceptor {

    private static final Map<Long, Integer> roomUserCount = new ConcurrentHashMap<>();

    private static final int MAX_USER = 2;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        URI uri = request.getURI();
        String query = uri.getQuery();
        Long roomId = Long.valueOf(query.split("=")[1]);

        int current = roomUserCount.getOrDefault(roomId, 0);

        if (current >= MAX_USER) {
            log.warn("방이 가득 찼습니다. roomId={}, current={}", roomId, current);
            return false;
        }

        roomUserCount.put(roomId, current+1);
        attributes.put("roomId", roomId);
        log.info("사용자 입장: roomId={}, current={}", roomId, roomUserCount.get(roomId));

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    public static void decreaseUser(Long roomId){
        roomUserCount.computeIfPresent(roomId, (id, count) -> count - 1);
        log.info("사용자 퇴장: roomId={}, current={}", roomId, roomUserCount.get(roomId));
    }
}
