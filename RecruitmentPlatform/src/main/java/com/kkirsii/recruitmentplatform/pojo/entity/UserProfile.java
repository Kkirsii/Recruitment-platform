package com.kkirsii.recruitmentplatform.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("user_profile")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    @TableId(value = "email",type = IdType.INPUT)
    private String email;

    @TableField("resume_filename")
    private String resumeFilename;

    @TableField("photo_filename")
    private String photoFilename;
}
