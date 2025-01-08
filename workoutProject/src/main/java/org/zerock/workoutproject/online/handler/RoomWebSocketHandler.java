package org.zerock.workoutproject.online.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class RoomWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, CopyOnWriteArrayList<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = extractRoomId(session.getUri().toString());
        rooms.computeIfAbsent(roomId, r -> new CopyOnWriteArrayList<>()).add(session);
        System.out.println("WebSocket 연결 성공 - Room ID: " + roomId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = extractRoomId(session.getUri().toString());
        rooms.getOrDefault(roomId, new CopyOnWriteArrayList<>()).remove(session);
        System.out.println("WebSocket 연결 종료 - Room ID: " + roomId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JsonNode jsonNode = objectMapper.readTree(payload);
        String type = jsonNode.get("type").asText();
        JsonNode data = jsonNode.get("data");

        String roomId = extractRoomId(session.getUri().toString());

        switch (type) {
            case "JOIN":
                break;

            default:
                broadcastToRoom(roomId, payload, session);
                break;
        }
    }

    private void broadcastToRoom(String roomId, String message, WebSocketSession sender) {
        CopyOnWriteArrayList<WebSocketSession> sessions = rooms.getOrDefault(roomId, new CopyOnWriteArrayList<>());
        for (WebSocketSession s : sessions) {
            if (s.isOpen() && s != sender) {
                try {
                    s.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void broadcastToRoom(String roomId, String message) {
        broadcastToRoom(roomId, message, null);
    }

    private String extractRoomId(String path) {
        if (!path.contains("/ws/room/")) return "";
        String[] parts = path.split("/ws/room/");
        if (parts.length < 2) return "";
        return parts[1];
    }
}