package xyz.lingview.chat.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.lingview.chat.domain.Login;
import xyz.lingview.chat.mapper.ControlsMapper;
import xyz.lingview.chat.tools.Jwt;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private ControlsMapper controlsMapper;

    @Autowired
    private Jwt jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> response = new HashMap<>();
        Login login = new Login();
        login.setUsername(username);
        login.setPassword(password);

        Login result = controlsMapper.loginUser(login);
        if (result == null || !result.getPassword().equals(password)) {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } else {
            String token = jwtUtils.generateToken(result.getUsername());
//            result.setToken(token);
//            controlsMapper.updateToken(result.getUsername(), token);
            response.put("success", true);
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
    }
}
