package com.kkirsii.recruitmentplatform.server.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kkirsii.recruitmentplatform.mapper.JobInfoMapper;
import com.kkirsii.recruitmentplatform.mapper.SubmitStateMapper;
import com.kkirsii.recruitmentplatform.pojo.entity.JobInfo;
import com.kkirsii.recruitmentplatform.pojo.entity.SubmitState;
import com.kkirsii.recruitmentplatform.server.AdminJb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminJbImpl implements AdminJb {

    @Autowired
    JobInfoMapper jobInfoMapper;
    @Autowired
    SubmitStateMapper submitStateMapper;
    @Override
    public void publish(JobInfo jobInfo) {
        jobInfoMapper.insert(jobInfo);


    }

    @Override
    public void delete(int id) {
        jobInfoMapper.deleteById(id);
    }

    @Override
    public void update(JobInfo jobInfo) {
        jobInfoMapper.updateById(jobInfo);
    }

    @Override
    public List<JobInfo> getlist(int page, int size) {
        IPage<JobInfo> rpage = new Page<>(page, size);
        IPage<JobInfo> resultPage = jobInfoMapper.selectPage(rpage, null);
        return resultPage.getRecords(); // 转换成 List 返回
    }

    @Override
    public List<SubmitState> getSubmitList(int page, int size) {
        IPage<SubmitState>rpage=new Page<>(page, size);
        IPage<SubmitState> resultPage = submitStateMapper.selectPage(rpage, null);
        return resultPage.getRecords();
    }

    @Override
    public boolean changeSubmitState(int id, int state) {
        SubmitState submitState = submitStateMapper.selectById(id);
        submitState.setJobState(state);
        submitStateMapper.updateById(submitState);
        return true;
    }
}
