package com.kkirsii.recruitmentplatform.server;

import com.kkirsii.recruitmentplatform.pojo.entity.JobInfo;

import java.util.List;

public interface UserJb {
    List<JobInfo> getlist(int page, int size);

    boolean submit(int id);
}
