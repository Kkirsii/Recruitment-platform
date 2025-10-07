package com.kkirsii.recruitmentplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kkirsii.recruitmentplatform.pojo.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    @Update("update user_info set image_name = #{fileName} where email = #{email}")
    void updateImageNameByEmail(@Param("fileName") String fileName, @Param("email")String email);

    @Update("update user_info set resume_name = #{fileName} where email = #{email}")
    void updatateResumeNameByEmail(@Param("fileName") String fileName, @Param("email")String email);
}
