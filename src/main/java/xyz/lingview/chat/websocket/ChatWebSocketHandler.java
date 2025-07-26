// ChatWebSocketHandler.java
package xyz.lingview.chat.websocket;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    // 存储房间内的用户连接
    private final Map<String, Map<String, WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 连接建立时不需要特殊处理
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            // 解析客户端发送的消息
            String payload = message.getPayload();
            ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(payload);

            String type = jsonNode.get("type").asText();

            switch (type) {
                case "join":
                    handleJoinRoom(session, jsonNode);
                    break;
                case "message":
                    handleChatMessage(session, jsonNode);
                    break;
                case "leave":
                    handleLeaveRoom(session, jsonNode);
                    break;
                default:
                    session.sendMessage(new TextMessage("{\"error\": \"未知消息类型\"}"));
                    break;
            }
        } catch (Exception e) {
            session.sendMessage(new TextMessage("{\"error\": \"消息处理失败: " + e.getMessage() + "\"}"));
        }
    }

    private void handleJoinRoom(WebSocketSession session, ObjectNode jsonNode) throws Exception {
        String roomId = jsonNode.get("roomId").asText();
        String username = jsonNode.get("username").asText();

        // 将用户会话添加到房间中
        roomSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
                   .put(username, session);

        // 向加入者发送欢迎消息
        ObjectNode welcomeMsg = objectMapper.createObjectNode();
        welcomeMsg.put("type", "welcome");
        welcomeMsg.put("message", "欢迎加入聊天室");
        session.sendMessage(new TextMessage(welcomeMsg.toString()));

        // 通知房间内其他用户有新用户加入
        ObjectNode joinMsg = objectMapper.createObjectNode();
        joinMsg.put("type", "userJoined");
        joinMsg.put("username", username);
        joinMsg.put("timestamp", System.currentTimeMillis());
        broadcastToRoom(roomId, username, joinMsg.toString());
    }

    private void handleChatMessage(WebSocketSession session, ObjectNode jsonNode) throws Exception {
        String roomId = jsonNode.get("roomId").asText();
        String username = jsonNode.get("username").asText();
        String content = jsonNode.get("content").asText();

        // 创建消息对象
        ObjectNode messageObj = objectMapper.createObjectNode();
        messageObj.put("type", "message");
        messageObj.put("username", username);
        messageObj.put("content", content);
        messageObj.put("timestamp", System.currentTimeMillis());

        // 广播消息给房间内所有用户
        broadcastToRoom(roomId, username, messageObj.toString());
    }

    private void handleLeaveRoom(WebSocketSession session, ObjectNode jsonNode) throws Exception {
        String roomId = jsonNode.get("roomId").asText();
        String username = jsonNode.get("username").asText();

        // 从房间中移除用户
        if (roomSessions.containsKey(roomId)) {
            roomSessions.get(roomId).remove(username);

            // 通知房间内其他用户该用户已离开
            ObjectNode leaveMsg = objectMapper.createObjectNode();
            leaveMsg.put("type", "userLeft");
            leaveMsg.put("username", username);
            leaveMsg.put("timestamp", System.currentTimeMillis());
            broadcastToRoom(roomId, username, leaveMsg.toString());

            // 如果房间为空，清理房间
            if (roomSessions.get(roomId).isEmpty()) {
                roomSessions.remove(roomId);
            }
        }
    }

    private void broadcastToRoom(String roomId, String sender, String message) throws Exception {
        if (roomSessions.containsKey(roomId)) {
            for (Map.Entry<String, WebSocketSession> entry : roomSessions.get(roomId).entrySet()) {
                WebSocketSession session = entry.getValue();
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (Exception e) {
                        // 处理发送失败的情况
                        System.err.println("发送消息失败: " + e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 清理关闭的连接
        cleanupClosedSession(session);
    }

    private void cleanupClosedSession(WebSocketSession closedSession) {
        // 遍历所有房间，移除已关闭的会话
        for (Map<String, WebSocketSession> room : roomSessions.values()) {
            room.entrySet().removeIf(entry -> {
                WebSocketSession session = entry.getValue();
                if (session.getId().equals(closedSession.getId())) {
                    // 通知其他用户该用户已离开
                    try {
                        ObjectNode leaveMsg = objectMapper.createObjectNode();
                        leaveMsg.put("type", "userLeft");
                        leaveMsg.put("username", entry.getKey());
                        leaveMsg.put("timestamp", System.currentTimeMillis());
                        // 广播给其他用户（除了正在清理的这个）
                        for (Map.Entry<String, WebSocketSession> otherEntry : room.entrySet()) {
                            if (!otherEntry.getKey().equals(entry.getKey()) && otherEntry.getValue().isOpen()) {
                                otherEntry.getValue().sendMessage(new TextMessage(leaveMsg.toString()));
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("发送用户离开消息失败: " + e.getMessage());
                    }
                    return true;
                }
                return false;
            });
        }

        // 清理空房间
        roomSessions.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
}
