package com.example.chat.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

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

        Long roomId = UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .getFirst("roomId") != null
                ? Long.valueOf(UriComponentsBuilder.fromUri(uri).build().getQueryParams().getFirst("roomId"))
                : null;

        if (roomId == null) {
            log.warn("roomId 누락: {}", uri);
            return false;
        }

        int current = roomUserCount.merge(roomId, 1, Integer::sum);

        if (current > MAX_USER) {
            roomUserCount.merge(roomId, -1, Integer::sum);
            log.warn("방이 가득 찼습니다. roomId={}, current={}", roomId, current-1);
            return false;
        }

        attributes.put("roomId", roomId);
        log.info("사용자 입장: roomId={}, current={}", roomId, current);

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
