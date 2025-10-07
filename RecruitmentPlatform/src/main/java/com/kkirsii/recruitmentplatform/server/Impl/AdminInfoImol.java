package com.kkirsii.recruitmentplatform.server.Impl;

import com.kkirsii.recruitmentplatform.mapper.JobInfoMapper;
import com.kkirsii.recruitmentplatform.mapper.UserInfoMapper;
import com.kkirsii.recruitmentplatform.pojo.entity.InvitationInfo;
import com.kkirsii.recruitmentplatform.server.AdminInfo;
import com.kkirsii.recruitmentplatform.util.YamlConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
@Slf4j
public class AdminInfoImol implements AdminInfo {
    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    JobInfoMapper JobInfoMapper;


    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private JobInfoMapper jobInfoMapper;

    @Override
    public void getResume(HttpServletResponse response, String email) {
        try {
            var userInfo = userInfoMapper.selectById(email);
            if (userInfo == null) {
                log.warn("用户不存在: {}", email);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "用户不存在");
                return;
            }
            
            String fileName = userInfo.getResumeName();
            if (fileName == null || fileName.isEmpty()) {
                log.info("用户 {} 暂无简历", email);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "用户暂无简历");
                return;
            }
            
            String ResumePath = YamlConfig.getResumePath();
            byte[] fileBytes = YamlConfig.downloadFile(ResumePath, fileName);

            if (fileBytes == null || fileBytes.length == 0) {
                log.info("简历文件不存在: {}", fileName);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "简历文件不存在");
                return;
            }
            
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + email + "_resume.pdf\"");
            response.setContentLength(fileBytes.length);
            
            try (OutputStream os = response.getOutputStream()) {
                os.write(fileBytes);
                os.flush();
            }
        } catch (IOException e) {
            log.error("响应简历异常: {}", e.getMessage());
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
            } catch (IOException ioException) {
                log.error("发送错误响应失败: {}", ioException.getMessage());
            }
        }
    }

    @Override
    public void getImage(HttpServletResponse response, String email) {
        try {
            var userInfo = userInfoMapper.selectById(email);
            if (userInfo == null) {
                log.warn("用户不存在: {}", email);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "用户不存在");
                return;
            }
            
            String fileName = userInfo.getImageName();
            if (fileName == null || fileName.isEmpty()) {
                log.info("用户 {} 暂无头像", email);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "用户暂无头像");
                return;
            }
            
            String ImagePath = YamlConfig.getImagePath();
            byte[] fileBytes = YamlConfig.downloadFile(ImagePath, fileName);
            
            if (fileBytes == null || fileBytes.length == 0) {
                log.info("头像文件不存在: {}", fileName);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "头像文件不存在");
                return;
            }
            
            response.setContentType("image/jpeg");
            response.setContentLength(fileBytes.length);
            
            try (OutputStream os = response.getOutputStream()) {
                os.write(fileBytes);
                os.flush();
            }
        } catch (IOException e) {
            log.error("响应图片异常: {}", e.getMessage());
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
            } catch (IOException ioException) {
                log.error("发送错误响应失败: {}", ioException.getMessage());
            }
        }
    }






    @Override
    public void sendInvitation(String email, Integer jobId, String location, LocalTime time, LocalDate date) {
        InvitationInfo invitationInfo = new InvitationInfo();
        invitationInfo.setContactEmail("2069452598@qq.com");
        invitationInfo.setJobType(JobInfoMapper.selectById(jobId).getName());
        invitationInfo.setLocation(location);
        invitationInfo.setTime(time);
        invitationInfo.setDate(date);
        String subject = "面试邀请 - " + invitationInfo.getJobType();
        String text = String.format("尊敬的%s，\n\n" +
                        "我们很高兴地通知您，您已被选中参加我们公司的%s职位的面试。\n\n" +
                        "面试详情如下：\n" +
                        "日期: %s\n" +
                        "时间: %s\n" +
                        "地点: %s\n\n" +
                        "请携带您的身份证和相关证件准时参加面试。如有任何问题，请随时通过此邮件与我们联系。\n\n" +
                        "祝您好运！\n\n" +
                        "此致，\n%s",
                userInfoMapper.selectById(email).getName(),
                invitationInfo.getJobType(),
                invitationInfo.getDate().toString(),
                invitationInfo.getTime().toString(),
                invitationInfo.getLocation(),
                invitationInfo.getContactEmail()
        );

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("2069452598@qq.com");
            message.setTo(email);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("向用户 {} 发送面试邀请邮件", email);


    }

    @Override
    public void send_offer(String email, Integer jobId) {
        String subject = "录用通知 - " + JobInfoMapper.selectById(jobId).getName();
        String text = String.format("尊敬的%s，\n\n" +
                        "恭喜您！我们很高兴地通知您，您已被录用为我们公司的%s职位。\n\n" +
                        "我们相信您的技能和经验将为我们的团队带来宝贵的贡献。\n\n" +
                        "请您尽快与我们联系，以便我们安排入职事宜。如有任何问题，请随时通过此邮件与我们联系。\n\n" +
                        "期待您的加入！\n\n" +
                        "此致，\n%s",
                userInfoMapper.selectById(email).getName(),
                JobInfoMapper.selectById(jobId).getName(),
                "2069452598@qq.com");
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("2069452598@qq.com");
            message.setTo(email);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);

    }


}
