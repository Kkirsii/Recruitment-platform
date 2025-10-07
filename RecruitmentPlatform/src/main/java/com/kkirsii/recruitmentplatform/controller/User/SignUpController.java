package com.kkirsii.recruitmentplatform.controller.User;

import com.kkirsii.recruitmentplatform.pojo.DTO.Email;
import com.kkirsii.recruitmentplatform.pojo.DTO.SignUpInfo;
import com.kkirsii.recruitmentplatform.pojo.VO.Result;
import com.kkirsii.recruitmentplatform.pojo.entity.User;
import com.kkirsii.recruitmentplatform.server.SignUp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/signup")
public class SignUpController {
    private static final Logger log = LoggerFactory.getLogger(SignUpController.class);
    @Autowired
    private SignUp signUp;
    @PostMapping("/getcode")
    public Result getcode(@RequestBody Email email){
        log.info("发送验证码到: " + email.getEmail());
        signUp.getcode(email);
        return Result.success();
    }

    @PostMapping
    public Result signup(@RequestBody SignUpInfo signUpInfo){
        log.info("用户注册尝试: " + signUpInfo.getEmail());
        boolean c=signUp.signup(signUpInfo);
        if(c)
        return Result.success();
        return Result.error();
    }


}
