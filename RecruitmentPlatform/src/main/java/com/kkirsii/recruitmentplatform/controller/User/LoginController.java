package com.kkirsii.recruitmentplatform.controller.User;

import com.kkirsii.recruitmentplatform.pojo.VO.Result;
import com.kkirsii.recruitmentplatform.pojo.entity.Admin;
import com.kkirsii.recruitmentplatform.pojo.entity.User;
import com.kkirsii.recruitmentplatform.server.Login;
import com.kkirsii.recruitmentplatform.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    Login login;

    @PostMapping("/user/login")
    public Result login(@RequestBody User user) {
            log.info("用户登录尝试: " + user.getEmail());
            boolean c=login.lg(user);
            if(!c){return Result.error();}
            String jwt= JwtUtil.generateToken(user.getEmail(),"user");
            return Result.success(jwt);
    }

    @PostMapping("/admin/login")
    public Result login(@RequestBody Admin admin) {
            log.info("管理员登录尝试: " + admin.getEmail());
            boolean c=login.alg(admin);
            if(!c){return Result.error();}
            String jwt= JwtUtil.generateToken(admin.getEmail(),"admin");
            return Result.success(jwt);

    }
}
