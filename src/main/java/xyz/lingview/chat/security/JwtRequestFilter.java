package xyz.lingview.chat.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.lingview.chat.service.UserBlacklistService;
import xyz.lingview.chat.tools.Jwt;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private Jwt jwtUtil;

    @Autowired
    private UserBlacklistService userBlacklistService;

    // 不需要拦截的路径
    private static final String[] EXCLUDED_PATHS = {
            "/api/login", "/api/register", "/api/admin/login"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (request.getMethod().equals("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }

        String path = request.getServletPath();

        // 判断是否为免登录路径
        boolean shouldExclude = false;
        for (String excludedPath : EXCLUDED_PATHS) {
            if (path.equals(excludedPath)) {
                shouldExclude = true;
                break;
            }
        }

        if (shouldExclude) {
            chain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authorizationHeader);

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println("Extracted JWT: " + jwt);

            try {
                // 提取用户名
                username = jwtUtil.extractUsername(jwt);

                // 验证 Token 是否过期
                if (jwtUtil.isTokenExpired(jwt)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
                    return;
                }

                // 验证 Token 合法性
                if (!jwtUtil.validateToken(jwt, username)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
                    return;
                }

                // 检查用户是否在黑名单中
                if (userBlacklistService.isUserInBlacklist(username)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "该用户已被拉黑");
                    return;
                }

            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
                return;
            }

        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        if (username != null && request.getAttribute("username") == null) {
            request.setAttribute("username", username);
            System.out.println("Set username in request attribute: " + username);
        }

        chain.doFilter(request, response);
    }
}
