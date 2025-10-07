package com.kkirsii.recruitmentplatform.server.Impl;

import com.kkirsii.recruitmentplatform.mapper.AdminMapper;
import com.kkirsii.recruitmentplatform.mapper.UserMapper;
import com.kkirsii.recruitmentplatform.pojo.entity.Admin;
import com.kkirsii.recruitmentplatform.pojo.entity.User;
import com.kkirsii.recruitmentplatform.server.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginImpl implements Login {
    @Autowired
    UserMapper userMapper;
    @Autowired
    AdminMapper adminMapper;
    @Override
    public boolean lg(User user) {
        User user1=userMapper.selectById(user.getEmail());
        if(user1==null)
        {
            return false;
        }


        if(user1.getPassword().equals(user.getPassword())) {
            return true;
        }

        return false;
    }

    @Override
    public boolean alg(Admin admin) {
        Admin admin1=adminMapper.selectById(admin.getEmail());
        if(admin1==null)
        {
            return false;
        }
        if(admin1.getPassword().equals(admin.getPassword())) {
            return true;
        }
        return false;
    }
}
