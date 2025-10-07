package com.kkirsii.recruitmentplatform.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.kkirsii.recruitmentplatform.ENUM.JobState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("submit_state")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitState {
    private int id;
    private String email;
    private int jobState;
    private long jobId;
    private String jobName;
}
