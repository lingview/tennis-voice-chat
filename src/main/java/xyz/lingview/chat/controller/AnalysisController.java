package xyz.lingview.chat.controller;

import org.springframework.web.bind.annotation.*;
import xyz.lingview.chat.tools.CommunicationAnalyzer;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @PostMapping("/communication")
    public Map<String, Object> analyzeCommunication(@RequestBody Map<String, String> payload,
                                                   HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = (String) request.getAttribute("username");

            String message = payload.get("message");
            String emotion = payload.get("emotion");

            if (message == null || message.isEmpty()) {
                response.put("success", false);
                response.put("message", "消息内容不能为空");
                return response;
            }

            if (emotion == null || emotion.isEmpty()) {
                emotion = "未知";
            }

            String analysisResult = CommunicationAnalyzer.analyzeCommunicationWithLLM(message, emotion);

            response.put("success", true);
            response.put("analysis", analysisResult);
            response.put("message", "分析完成");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "分析失败: " + e.getMessage());
        }

        return response;
    }
}
