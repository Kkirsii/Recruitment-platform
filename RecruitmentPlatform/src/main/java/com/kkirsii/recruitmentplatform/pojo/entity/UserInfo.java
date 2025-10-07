package com.kkirsii.recruitmentplatform.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kkirsii.recruitmentplatform.ENUM.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("user_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    @TableId(value = "email",type = IdType.INPUT)
    private String email;
    private String name;
    private Long age;

    private Gender gender;
    private String university;
    private String major;
    private String phone;
    @TableField("image_name")
    private String imageName;
    @TableField("resume_name")
    private String resumeName;
}
