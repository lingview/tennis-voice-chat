package xyz.lingview.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.lingview.chat.domain.Register;
import xyz.lingview.chat.mapper.ControlsMapper;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegisterController {

    @Autowired
    ControlsMapper controlsMapper;
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Register register) {
        Map<String, Object> response = new HashMap<>();

        String username = register.getUsername();
        String email = register.getEmail();
        String password = register.getPassword();

        System.out.println("注册用户名：" + username + " 邮箱：" + email + " 密码：" + password);

        int result = controlsMapper.selectUser(username);
        if (result == 1) {
            response.put("success", false);
            response.put("message", "账号已存在！");
            System.out.println("账号已存在！");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        int insertUserResult = controlsMapper.insertUser(register);
        if (insertUserResult > 0) {
            response.put("success", true);
            response.put("message", "注册成功！");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "注册失败，请稍后再试！");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
