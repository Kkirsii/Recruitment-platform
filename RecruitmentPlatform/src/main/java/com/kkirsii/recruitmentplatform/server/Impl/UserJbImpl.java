package com.kkirsii.recruitmentplatform.server.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kkirsii.recruitmentplatform.mapper.JobInfoMapper;
import com.kkirsii.recruitmentplatform.mapper.SubmitStateMapper;
import com.kkirsii.recruitmentplatform.pojo.entity.JobInfo;
import com.kkirsii.recruitmentplatform.pojo.entity.SubmitState;
import com.kkirsii.recruitmentplatform.server.UserJb;
import com.kkirsii.recruitmentplatform.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserJbImpl implements UserJb {
    @Autowired
    JobInfoMapper jobInfoMapper;
    @Autowired
    SubmitStateMapper submitStateMapper;
    @Override
    public List<JobInfo> getlist(int page, int size) {
        IPage<JobInfo>page1=new Page<>(page,size);
        IPage<JobInfo>result=jobInfoMapper.selectPage(page1,null);
        return result.getRecords();
    }

    @Override
    //id为job_id
    public boolean submit(int id) {
        String email= UserContext.getEmail();
        QueryWrapper<SubmitState> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        List<SubmitState> result = submitStateMapper.selectList(queryWrapper);
        if(result.size()>2){
            return false;
        }
        SubmitState submitState=new SubmitState();
        submitState.setEmail(email);
        submitState.setJobState(0);
        JobInfo jobInfo=jobInfoMapper.selectById(id);
        submitState.setJobId(id);
        submitState.setJobName(jobInfo.getName());
        submitStateMapper.insert(submitState);

        return true;
    }
}
