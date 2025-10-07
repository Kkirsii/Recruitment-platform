package com.kkirsii.recruitmentplatform.server.Impl;

import com.kkirsii.recruitmentplatform.mapper.UserInfoMapper;
import com.kkirsii.recruitmentplatform.mapper.UserProfileMapper;
import com.kkirsii.recruitmentplatform.pojo.entity.UserInfo;
import com.kkirsii.recruitmentplatform.pojo.entity.UserProfile;
import com.kkirsii.recruitmentplatform.server.UserIf;
import com.kkirsii.recruitmentplatform.util.UserContext;

import com.kkirsii.recruitmentplatform.util.YamlConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;


import com.kkirsii.recruitmentplatform.config.RedisConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import org.yaml.snakeyaml.Yaml;


@Service
@Slf4j
public class UserIfImpl implements UserIf {


    @Autowired
    private RedisTemplate<String,byte[]> redisTemplate;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;

    @Override
    public void submit(UserInfo userInfo) {

        String email = UserContext.getEmail();
        userInfo.setEmail(email);
        UserInfo temp = userInfoMapper.selectById(email);
        if (temp != null) {
            userInfoMapper.updateById(userInfo);
            log.info("修改信息");
            return;
        }
        userInfoMapper.insert(userInfo);
        log.info("上传基本信息成功");
    }

    @Override
    public UserInfo get() {
        UserInfo temp = userInfoMapper.selectById(UserContext.getEmail());
        return temp;
    }

    @Override
    public boolean uploadImage(MultipartFile file) {
        String ImagePath = YamlConfig.getImagePath();
        String email = UserContext.getEmail();
        //获取文件后缀名
        String originalFilename = file.getOriginalFilename(); // e.g., "photo.png"
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        //构建文件名
        String FileName = email + extension;
        try{
            YamlConfig.uploadFile(file, ImagePath, FileName);
            userInfoMapper.updateImageNameByEmail(FileName, email);
            log.info("上传成功，文件保存为：{}", ImagePath+FileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    public void getImage(HttpServletResponse response) {

        String ImagePath = YamlConfig.getImagePath();
        String fileName = userInfoMapper.selectById(UserContext.getEmail()).getImageName();
//        String CacheKey="Image."+fileName;
//        //先从缓存中取
//        if(redisTemplate.hasKey(CacheKey))
//        {
//            byte[] fileBytes= (byte[]) redisTemplate.opsForValue().get(CacheKey);
//            response.setContentType("image/jpeg");
//            try (OutputStream os = response.getOutputStream()) {
//                os.write(fileBytes);
//                os.flush();
//            } catch (IOException e) {
//                log.error("响应图片异常: {}", e.getMessage());
//                throw new RuntimeException(e);
//            }
//            return;
//        }
        byte[] fileBytes = YamlConfig.downloadFile(ImagePath, fileName);
//        //放入缓存
//        if(fileBytes!=null)
//        {
//            redisTemplate.opsForValue().set(CacheKey,fileBytes,5*60, java.util.concurrent.TimeUnit.SECONDS);
//        }
        if (fileBytes == null) {
            log.info("图片不存在");
            return;
        }
        response.setContentType("image/jpeg");
        try (OutputStream os = response.getOutputStream()) {
            os.write(fileBytes);
            os.flush();
        } catch (IOException e) {
            log.error("响应图片异常: {}", e.getMessage());
            throw new RuntimeException(e);
        }



    }

    @Override
    public boolean resumeUpload(MultipartFile file) {
        String ResumePath = YamlConfig.getResumePath();
        String email = UserContext.getEmail();
        //获取文件后缀名
        String originalFilename = file.getOriginalFilename(); // e.g., "document.pdf"
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        //构建文件名
        String FileName = email + extension;
        try {
            YamlConfig.uploadFile(file, ResumePath, FileName);
            userInfoMapper.updatateResumeNameByEmail(FileName, email);


        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public void getResume(HttpServletResponse response) {
        String ResumePath = YamlConfig.getResumePath();
        String fileName = userInfoMapper.selectById(UserContext.getEmail()).getResumeName();
        byte[] fileBytes = YamlConfig.downloadFile(ResumePath, fileName);
        if (fileBytes == null) {
            log.info("简历不存在");
            return;
        }
        response.setContentType("application/pdf");
        try (OutputStream os = response.getOutputStream()) {
            os.write(fileBytes);
            os.flush();
        } catch (IOException e) {
            log.error("响应简历异常: {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }


}
