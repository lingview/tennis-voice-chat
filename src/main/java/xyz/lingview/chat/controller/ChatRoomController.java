// ChatRoomController.java
package xyz.lingview.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.lingview.chat.domain.ChatRoom;
import xyz.lingview.chat.mapper.ControlsMapper;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatRoomController {

    @Autowired
    private ControlsMapper controlsMapper;

    /**
     * 创建聊天室
     */
    @PostMapping("/create")
    public Map<String, Object> createChatRoom(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 从请求属性中获取用户名（由JWT拦截器解析）
            String username = (String) request.getAttribute("username");

            // 生成房间唯一ID
            String roomUuid = UUID.randomUUID().toString();

            // 创建聊天室记录
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setUuid(roomUuid);
            chatRoom.setCreate_user(username);
            chatRoom.setStatus(1); // 正常状态
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

    /**
     * 加入聊天室
     */
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

            // 检查房间是否存在且有效
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

    /**
     * 关闭聊天室
     */
    @PostMapping("/close")
    public Map<String, Object> closeChatRoom(@RequestBody Map<String, String> payload,
                                            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 从请求属性中获取用户名（由JWT拦截器解析）
            String username = (String) request.getAttribute("username");

            String roomId = payload.get("roomId");
            if (roomId == null || roomId.isEmpty()) {
                response.put("success", false);
                response.put("message", "房间ID不能为空");
                return response;
            }

            // 检查房间是否存在且由当前用户创建
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

            // 关闭房间
            controlsMapper.closeChatRoom(roomId);

            response.put("success", true);
            response.put("message", "房间已关闭");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "关闭房间失败: " + e.getMessage());
        }

        return response;
    }

    /**
     * 查询聊天室信息
     */
    @GetMapping("/info/{roomId}")
    public Map<String, Object> getChatRoomInfo(@PathVariable String roomId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 从请求属性中获取用户名（由JWT拦截器解析）
            String username = (String) request.getAttribute("username");

            if (roomId == null || roomId.isEmpty()) {
                response.put("success", false);
                response.put("message", "房间ID不能为空");
                return response;
            }

            // 检查房间是否存在且有效
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

    // 在ChatRoomController.java中添加以下方法
    /**
     * 获取当前用户信息
     */
    @GetMapping("/user/info")
    public Map<String, Object> getCurrentUserInfo(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 从请求属性中获取用户名（由JWT拦截器解析）
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
}
