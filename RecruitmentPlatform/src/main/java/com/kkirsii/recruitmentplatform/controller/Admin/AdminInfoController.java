package com.kkirsii.recruitmentplatform.controller.Admin;

import com.kkirsii.recruitmentplatform.mapper.SubmitStateMapper;
import com.kkirsii.recruitmentplatform.pojo.entity.SubmitState;
import com.kkirsii.recruitmentplatform.server.AdminInfo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/admin/userinfo")
public class AdminInfoController {

    @Autowired
    private AdminInfo adminInfo;

    @Autowired
    private SubmitStateMapper submitStateMapper;

    @GetMapping("/resume")
    public void getResume(HttpServletResponse response, @RequestParam("email") String email) {
        log.info("获取用户简历: {}", email);
        adminInfo.getResume(response, email);
    }

    @GetMapping("/Image")
    public void getImage(HttpServletResponse response, @RequestParam("email") String email) {
        log.info("获取用户头像: {}", email);
        adminInfo.getImage(response, email);
    }

    /**
     * 0: 未处理
     * 1: 待发送面试邀请
     * 2: 已发送面试邀请
     * 3: 面试通过
     * 4: 不匹配
     * 5: 放弃申请
     * 6: 已录用
     */
    @PutMapping("push_1")
    public void push_1(@RequestParam("email") String email, @RequestParam("jobId") Integer jobId) {
        log.info("将用户 {} 的职位 {} 状态改为 1", email, jobId);
        submitStateMapper.updateState(email, jobId, 1);
    }
    @PutMapping("send_invitation")
    public void push_2(@RequestParam("email") String email, @RequestParam("jobId") Integer jobId, @RequestParam("location") String location, @RequestParam("time") LocalTime time, @RequestParam("date") LocalDate date) {
        adminInfo.sendInvitation(email, jobId, location, time, date);
        log.info("将用户 {} 的职位 {} 状态改为 2", email, jobId);
        submitStateMapper.updateState(email, jobId, 2);
    }
    @PutMapping("push_3")
    public void push_3(@RequestParam("email") String email, @RequestParam("jobId") Integer jobId) {
        log.info("将用户 {} 的职位 {} 状态改为 3", email, jobId);
        submitStateMapper.updateState(email, jobId, 3);
    }
    @PutMapping("push_4")
    public void push_4(@RequestParam("email") String email, @RequestParam("jobId") Integer jobId) {
        log.info("将用户 {} 的职位 {} 状态改为 4", email, jobId);
        submitStateMapper.updateState(email, jobId, 4);
    }
    @PutMapping("push_5")
    public void push_5(@RequestParam("email") String email, @RequestParam("jobId") Integer jobId) {
        log.info("将用户 {} 的职位 {} 状态改为 5", email, jobId);
        submitStateMapper.updateState(email, jobId, 5);
    }
    @PutMapping("send_offer")
    public void push_6(@RequestParam("email") String email, @RequestParam("jobId") Integer jobId) {
        adminInfo.send_offer(email, jobId);
        log.info("将用户 {} 的职位 {} 状态改为 6", email, jobId);
        submitStateMapper.updateState(email, jobId, 6);
    }
}
