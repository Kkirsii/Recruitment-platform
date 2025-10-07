package com.kkirsii.recruitmentplatform.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("job_info")
public class JobInfo {

    private int id;
    private String name;
    private String number;
    private String demand;
    private String salary;
    private String welfare;
    private String location;
}
