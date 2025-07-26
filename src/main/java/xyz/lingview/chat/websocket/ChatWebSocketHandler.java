// ChatWebSocketHandler.java
package xyz.lingview.chat.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import xyz.lingview.chat.domain.Message;
import xyz.lingview.chat.mapper.ControlsMapper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = Logger.getLogger(ChatWebSocketHandler.class.getName());

    private final Map<String, Map<String, WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ControlsMapper controlsMapper;

    // 使用构造函数注入ControlsMapper
    public ChatWebSocketHandler(ControlsMapper controlsMapper) {
        this.controlsMapper = controlsMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("WebSocket连接已建立: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
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
                    ObjectNode errorMsg = objectMapper.createObjectNode();
                    errorMsg.put("type", "error");
                    errorMsg.put("error", "未知消息类型: " + type);
                    session.sendMessage(new TextMessage(errorMsg.toString()));
                    break;
            }
        } catch (Exception e) {
            logger.severe("处理消息时出错: " + e.getMessage());
            ObjectNode errorMsg = objectMapper.createObjectNode();
            errorMsg.put("type", "error");
            errorMsg.put("error", "消息处理失败: " + e.getMessage());
            session.sendMessage(new TextMessage(errorMsg.toString()));
        }
    }

    private void handleJoinRoom(WebSocketSession session, ObjectNode jsonNode) throws Exception {
        String roomId = jsonNode.get("roomId").asText();
        String username = jsonNode.get("username").asText();

        roomSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
                   .put(username, session);

        ObjectNode welcomeMsg = objectMapper.createObjectNode();
        welcomeMsg.put("type", "welcome");
        welcomeMsg.put("message", "欢迎加入聊天室");
        session.sendMessage(new TextMessage(welcomeMsg.toString()));

        ObjectNode joinMsg = objectMapper.createObjectNode();
        joinMsg.put("type", "userJoined");
        joinMsg.put("username", username);
        joinMsg.put("timestamp", System.currentTimeMillis());
        broadcastToRoom(roomId, username, joinMsg.toString());

        logger.info("用户 " + username + " 加入房间 " + roomId);
    }

    private void handleChatMessage(WebSocketSession session, ObjectNode jsonNode) throws Exception {
        String roomId = jsonNode.get("roomId").asText();
        String username = jsonNode.get("username").asText();
        String content = jsonNode.get("content").asText();

        // 保存消息到数据库
        try {
            Message message = new Message();
            message.setChat_room_id(roomId);
            message.setUsername(username);
            message.setChat(content);
            message.setStatus(1); // 正常状态
            // create_time字段由数据库自动处理

            logger.info("准备保存消息: roomId=" + roomId + ", username=" + username + ", content=" + content);
            controlsMapper.saveMessage(message);
            logger.info("消息已保存到数据库: " + username + " in room " + roomId + ": " + content);
        } catch (Exception e) {
            logger.severe("保存消息到数据库失败: " + e.getMessage());
            e.printStackTrace();
        }

        ObjectNode messageObj = objectMapper.createObjectNode();
        messageObj.put("type", "message");
        messageObj.put("username", username);
        messageObj.put("content", content);
        messageObj.put("timestamp", System.currentTimeMillis());

        broadcastToRoom(roomId, username, messageObj.toString());

        logger.info("房间 " + roomId + " 中用户 " + username + " 发送消息: " + content);
    }

    private void handleLeaveRoom(WebSocketSession session, ObjectNode jsonNode) throws Exception {
        String roomId = jsonNode.get("roomId").asText();
        String username = jsonNode.get("username").asText();

        if (roomSessions.containsKey(roomId)) {
            roomSessions.get(roomId).remove(username);

            ObjectNode leaveMsg = objectMapper.createObjectNode();
            leaveMsg.put("type", "userLeft");
            leaveMsg.put("username", username);
            leaveMsg.put("timestamp", System.currentTimeMillis());
            broadcastToRoom(roomId, username, leaveMsg.toString());

            if (roomSessions.get(roomId).isEmpty()) {
                roomSessions.remove(roomId);
            }

            logger.info("用户 " + username + " 离开房间 " + roomId);
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
                        logger.severe("向用户 " + entry.getKey() + " 发送消息失败: " + e.getMessage());
                        session.close();
                    }
                } else {
                    roomSessions.get(roomId).remove(entry.getKey());
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("WebSocket连接已关闭: " + session.getId() + ", 状态: " + status);
        cleanupClosedSession(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.severe("WebSocket传输错误: " + exception.getMessage());
        session.close(CloseStatus.SERVER_ERROR);
        cleanupClosedSession(session);
    }

    private void cleanupClosedSession(WebSocketSession closedSession) {
        for (Map.Entry<String, Map<String, WebSocketSession>> roomEntry : roomSessions.entrySet()) {
            String roomId = roomEntry.getKey();
            Map<String, WebSocketSession> room = roomEntry.getValue();

            for (Map.Entry<String, WebSocketSession> userEntry : room.entrySet()) {
                if (userEntry.getValue().getId().equals(closedSession.getId())) {
                    String username = userEntry.getKey();
                    room.remove(username);

                    try {
                        ObjectNode leaveMsg = objectMapper.createObjectNode();
                        leaveMsg.put("type", "userLeft");
                        leaveMsg.put("username", username);
                        leaveMsg.put("timestamp", System.currentTimeMillis());
                        broadcastToRoom(roomId, username, leaveMsg.toString());
                    } catch (Exception e) {
                        logger.severe("发送用户离开消息失败: " + e.getMessage());
                    }

                    break;
                }
            }

            if (room.isEmpty()) {
                roomSessions.remove(roomId);
            }
        }
    }
}
