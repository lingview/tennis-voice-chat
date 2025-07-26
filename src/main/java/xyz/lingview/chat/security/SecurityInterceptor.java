package xyz.lingview.chat.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);

    private static final Set<String> SENSITIVE_FIELDS = Set.of("token");

    private static final List<String> XSS_REGEX = List.of(
            "<script.*?>.*?</script>",
            "on\\w+\\s*=",
            "javascript:\\w+",
            "eval\\$\\$(.*?)\\$\\$");

    private static final List<String> SQLI_REGEX = List.of(
        "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(?:ALTER|CREATE|DELETE|DROP|EXEC(?:UTE)?|INSERT(?: +INTO)?|MERGE|SELECT|UPDATE|UNION(?: +ALL)?)\\b)",
        "\\bOR\\b\\s+\\d+=\\d+"
    );


    private static final Set<String> WHITELIST_VALUES = new HashSet<>();

    static {
        WHITELIST_VALUES.add("<p><strong>Rich text allowed</strong></p>");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIP(request);
        Map<String, String[]> params = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String paramName = entry.getKey();
            if (SENSITIVE_FIELDS.contains(paramName)) continue;

            for (String value : entry.getValue()) {
                if (WHITELIST_VALUES.contains(value)) continue;

                if (containsPatternByRegex(value, XSS_REGEX)) {
                    logAndReject(response, clientIp, paramName, value, "XSS");
                    return false;
                }
                if (containsPatternByRegex(value, SQLI_REGEX)) {
                    logAndReject(response, clientIp, paramName, value, "SQL Injection");
                    return false;
                }
            }
        }

        if ("POST".equalsIgnoreCase(request.getMethod()) && isJsonRequest(request)) {
            String body = readRequestBody(request);
            if (body != null) {
                if (containsPatternByRegex(body, XSS_REGEX)) {
                    logAndReject(response, clientIp, "json_body", body, "XSS");
                    return false;
                }
                if (containsPatternByRegex(body, SQLI_REGEX)) {
                    logAndReject(response, clientIp, "json_body", body, "SQL Injection");
                    return false;
                }
            }
        }

        return true;
    }

    // 获取客户端 IP
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    // 读取请求体
    private String readRequestBody(HttpServletRequest request) throws IOException {
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            byte[] contentAsByteArray = wrapper.getContentAsByteArray();
            return new String(contentAsByteArray, StandardCharsets.UTF_8);
        } else {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            return sb.toString();
        }
    }

    private boolean containsPatternByRegex(String input, List<String> regexList) {
        for (String pattern : regexList) {
            if (input.matches("(?i).*" + pattern + ".*")) {
                return true;
            }
        }
        return false;
    }

    private void logAndReject(HttpServletResponse response, String ip, String field, String value, String attackType) throws IOException {
        logger.warn("{} - [SECURITY] Attack Detected: {}, IP: {}, Field: {}, Value: {}", LocalDateTime.now(), attackType, ip, field, value);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, attackType + " attempt detected in field: " + field);
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.contains("application/json");
    }
}
