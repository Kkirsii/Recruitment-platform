package com.kkirsii.recruitmentplatform.server;


import com.kkirsii.recruitmentplatform.pojo.entity.Admin;
import com.kkirsii.recruitmentplatform.pojo.entity.User;

public interface Login {
    boolean lg(User user);

    boolean alg(Admin admin);
}
