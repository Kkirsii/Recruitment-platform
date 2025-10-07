package com.kkirsii.recruitmentplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kkirsii.recruitmentplatform.pojo.entity.SubmitState;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SubmitStateMapper extends BaseMapper<SubmitState> {


    @Update("update submit_state set job_state=#{i} where email=#{email} and job_id=#{jobId}")
    void updateState(@Param("email") String email, @Param("jobId") Integer jobId, @Param("i") int i);

    @Select("select * from submit_state where email=#{email}")
    List<SubmitState> selectByEmail(@Param("email") String email);
}
