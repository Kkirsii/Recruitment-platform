package com.kkirsii.recruitmentplatform.server;


import com.kkirsii.recruitmentplatform.pojo.DTO.Email;
import com.kkirsii.recruitmentplatform.pojo.DTO.SignUpInfo;
import com.kkirsii.recruitmentplatform.pojo.entity.User;

public interface SignUp {
    void getcode(Email email);

    boolean signup(SignUpInfo signUpInfo);
}
