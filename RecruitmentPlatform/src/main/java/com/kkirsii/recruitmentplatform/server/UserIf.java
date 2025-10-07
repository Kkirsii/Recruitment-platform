package com.kkirsii.recruitmentplatform.server;

import com.kkirsii.recruitmentplatform.pojo.entity.UserInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;


public interface UserIf {
    void submit(UserInfo userInfo);

    UserInfo get();

    boolean uploadImage(MultipartFile file);

    void getImage(HttpServletResponse response);

    boolean resumeUpload(MultipartFile file);


    void getResume(HttpServletResponse response);
}
