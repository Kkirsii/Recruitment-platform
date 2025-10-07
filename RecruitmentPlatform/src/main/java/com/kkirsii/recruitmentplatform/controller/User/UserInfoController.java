package com.kkirsii.recruitmentplatform.controller.User;

import com.kkirsii.recruitmentplatform.pojo.VO.Result;

import com.kkirsii.recruitmentplatform.pojo.entity.UserInfo;
import com.kkirsii.recruitmentplatform.server.UserIf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@Slf4j

@RequestMapping("/user/userinfo")
public class UserInfoController {


    @Autowired
    private UserIf userIf;

    @PostMapping()
    public Result submit(@RequestBody UserInfo userInfo) {

        userIf.submit(userInfo);

        return Result.success();
    }

    @CrossOrigin
    @GetMapping()
    public Result getUserInfo() {
        UserInfo userInfo = userIf.get();
        return Result.success(userInfo);
    }

    @PostMapping("imageupload")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("上传图片: " + file.getOriginalFilename());
        if (file.isEmpty()) {return Result.error("上传失败，文件为空");}
        boolean c= userIf.uploadImage(file);
        return  Result.success();
    }

    @GetMapping("/image")
    public void getImage(HttpServletResponse response) {
        log.info("获取图片");

        userIf.getImage( response);


    }

    @PostMapping("/resumeupload")
    public Result resumeUpload(@RequestParam("file") MultipartFile file) {
        log.info("上传简历: " + file.getOriginalFilename());
        boolean c=userIf.resumeUpload(file);
        if(c)
        {
            return Result.success();
        }

        return Result.error();
    }

    @GetMapping("/resume")
    public void getResume(HttpServletResponse response) {
        log.info("获取简历");

        userIf.getResume( response);
    }

}
