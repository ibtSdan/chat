package com.example.chat.interceptor;

import com.example.chat.websocket.RoomIdInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class RoomIdInterceptorTest {

    private RoomIdInterceptor interceptor;
    private ServerHttpRequest request;
    private ServerHttpResponse response;
    private WebSocketHandler handler;

    @BeforeEach
    void setUp() {
        interceptor = new RoomIdInterceptor();
        response = Mockito.mock(ServerHttpResponse.class);
        handler = Mockito.mock(WebSocketHandler.class);
    }

    @Test
    void 입장_가능하면_true() throws Exception {
        request = Mockito.mock(ServerHttpRequest.class);
        Mockito.when(request.getURI()).thenReturn(new URI("/we/chat?roomId=1"));

        Map<String, Object> attributes = new HashMap<>();

        boolean allowed = interceptor.beforeHandshake(request, response, handler, attributes);

        assertThat(allowed).isTrue();
        assertThat(attributes.get("roomId")).isEqualTo(1L);
    }

    @Test
    void 방_정원_초과면_false() throws Exception {
        request = Mockito.mock(ServerHttpRequest.class);
        Mockito.when(request.getURI()).thenReturn(new URI("/we/chat?roomId=2"));

        Map<String, Object> attributes = new HashMap<>();

        interceptor.beforeHandshake(request, response, handler, attributes);
        interceptor.beforeHandshake(request, response, handler, attributes);

        boolean result = interceptor.beforeHandshake(request, response, handler, attributes);

        assertThat(result).isFalse();
    }

    @Test
    void 퇴장하면_인원감소() {
        // 예외 없이 실행되는지
        RoomIdInterceptor.decreaseUser(1L);
    }

}
