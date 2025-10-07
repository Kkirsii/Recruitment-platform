package com.kkirsii.recruitmentplatform.server;

import com.kkirsii.recruitmentplatform.pojo.entity.JobInfo;
import com.kkirsii.recruitmentplatform.pojo.entity.SubmitState;

import java.util.List;

public interface AdminJb {
    void publish(JobInfo jobInfo);

    void delete(int id);

    void update(JobInfo jobInfo);

    List<JobInfo> getlist(int page, int size);

    List<SubmitState> getSubmitList(int page, int size);

    boolean changeSubmitState(int id, int state);
}
