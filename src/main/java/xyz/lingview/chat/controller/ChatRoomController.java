package xyz.lingview.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.lingview.chat.domain.ChatRoom;
import xyz.lingview.chat.domain.Message;
import xyz.lingview.chat.mapper.ControlsMapper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

@RestController
@RequestMapping("/api/chat")
public class ChatRoomController {

    @Autowired
    private ControlsMapper controlsMapper;

    @PostMapping("/create")
    public Map<String, Object> createChatRoom(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = (String) request.getAttribute("username");

            String roomUuid = UUID.randomUUID().toString();

            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setUuid(roomUuid);
            chatRoom.setCreate_user(username);
            chatRoom.setStatus(1);
            chatRoom.setCreate_time(new Date());

            controlsMapper.createChatRoom(chatRoom);

            response.put("success", true);
            response.put("roomId", roomUuid);
            response.put("message", "聊天室创建成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建聊天室失败: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/join")
    public Map<String, Object> joinChatRoom(@RequestBody Map<String, String> payload,
                                           HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = (String) request.getAttribute("username");

            String roomId = payload.get("roomId");
            if (roomId == null || roomId.isEmpty()) {
                response.put("success", false);
                response.put("message", "房间ID不能为空");
                return response;
            }

            ChatRoom chatRoom = controlsMapper.findChatRoomByUuid(roomId);
            if (chatRoom == null) {
                response.put("success", false);
                response.put("message", "房间不存在或已关闭");
                return response;
            }

            response.put("success", true);
            response.put("roomId", roomId);
            response.put("creator", chatRoom.getCreate_user());
            response.put("message", "成功加入房间");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "加入房间失败: " + e.getMessage());
        }

        return response;
    }


    @PostMapping("/close")
    public Map<String, Object> closeChatRoom(@RequestBody Map<String, String> payload,
                                            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = (String) request.getAttribute("username");

            String roomId = payload.get("roomId");
            if (roomId == null || roomId.isEmpty()) {
                response.put("success", false);
                response.put("message", "房间ID不能为空");
                return response;
            }

            ChatRoom chatRoom = controlsMapper.findChatRoomByUuid(roomId);
            if (chatRoom == null) {
                response.put("success", false);
                response.put("message", "房间不存在");
                return response;
            }

            if (!chatRoom.getCreate_user().equals(username)) {
                response.put("success", false);
                response.put("message", "只有创建者可以关闭房间");
                return response;
            }

            controlsMapper.closeChatRoom(roomId);

            response.put("success", true);
            response.put("message", "房间已关闭");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "关闭房间失败: " + e.getMessage());
        }

        return response;
    }


    @GetMapping("/info/{roomId}")
    public Map<String, Object> getChatRoomInfo(@PathVariable String roomId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {

            String username = (String) request.getAttribute("username");

            if (roomId == null || roomId.isEmpty()) {
                response.put("success", false);
                response.put("message", "房间ID不能为空");
                return response;
            }

            ChatRoom chatRoom = controlsMapper.findChatRoomByUuid(roomId);
            if (chatRoom == null) {
                response.put("success", false);
                response.put("message", "房间不存在或已关闭");
                return response;
            }

            response.put("success", true);
            response.put("roomId", chatRoom.getUuid());
            response.put("creator", chatRoom.getCreate_user());
            response.put("createTime", chatRoom.getCreate_time());
            response.put("message", "获取房间信息成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取房间信息失败: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/user/info")
    public Map<String, Object> getCurrentUserInfo(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = (String) request.getAttribute("username");

            if (username == null || username.isEmpty()) {
                response.put("success", false);
                response.put("message", "用户未认证");
                return response;
            }

            response.put("success", true);
            response.put("username", username);
            response.put("message", "获取用户信息成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取用户信息失败: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/history/{roomId}")
    public Map<String, Object> getChatHistory(@PathVariable String roomId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = (String) request.getAttribute("username");

            if (roomId == null || roomId.isEmpty()) {
                response.put("success", false);
                response.put("message", "房间ID不能为空");
                return response;
            }

            ChatRoom chatRoom = controlsMapper.findChatRoomByUuid(roomId);
            if (chatRoom == null) {
                response.put("success", false);
                response.put("message", "房间不存在或已关闭");
                return response;
            }

            List<Message> history = controlsMapper.getChatHistory(roomId);

            response.put("success", true);
            response.put("history", history);
            response.put("message", "获取聊天历史记录成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取聊天历史记录失败: " + e.getMessage());
        }

        return response;
    }
}
