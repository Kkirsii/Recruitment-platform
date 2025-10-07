package com.kkirsii.recruitmentplatform.server.Impl;

import com.kkirsii.recruitmentplatform.mapper.UserMapper;
import com.kkirsii.recruitmentplatform.pojo.DTO.Email;
import com.kkirsii.recruitmentplatform.pojo.DTO.SignUpInfo;
import com.kkirsii.recruitmentplatform.pojo.entity.User;
import com.kkirsii.recruitmentplatform.server.SignUp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SignUpImpl implements SignUp {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RedisTemplate redisTemplate;

    public String generateCode(){
        Random random=new Random();
        return String.format("%06d", random.nextInt(999999));
    }
    @Override
    public void getcode(Email email) {
        String code = generateCode();
        String key = "Code." + email.getEmail();
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("2069452598@qq.com");
        message.setTo(email.getEmail());
        message.setSubject("你的验证码");
        message.setText("你的验证码是：" + code + "，有效期5分钟，请及时使用。");
        log.info("向邮箱{}发送验证码", email.getEmail());
        mailSender.send(message);
    }

    @Override
    public boolean signup(SignUpInfo signUpInfo) {
        //检查是否已有该邮箱
        User user=userMapper.selectById(signUpInfo.getEmail());
        if(user!=null)
        {
            log.info("已有该账号");
            return false;
        }
        //检查验证码是否正确

        String key = "Code."+signUpInfo.getEmail();
        String code = (String) redisTemplate.opsForValue().get(key);
        if(!signUpInfo.getCode().equals(code))
        {
            log.info("验证码错误");
            return false;
        }
        user = new User(signUpInfo.getEmail(),signUpInfo.getPassword());
        log.info("注册成功");
        userMapper.insert(user);
        return true;
    }
}
