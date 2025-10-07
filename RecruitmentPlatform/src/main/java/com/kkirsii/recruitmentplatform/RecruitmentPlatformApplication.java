package com.kkirsii.recruitmentplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kkirsii.recruitmentplatform.mapper")
public class RecruitmentPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruitmentPlatformApplication.class, args);
    }

}
