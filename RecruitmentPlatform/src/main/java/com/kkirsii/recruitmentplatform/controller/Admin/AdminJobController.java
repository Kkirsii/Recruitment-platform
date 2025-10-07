package com.kkirsii.recruitmentplatform.controller.Admin;

import com.kkirsii.recruitmentplatform.pojo.VO.Result;
import com.kkirsii.recruitmentplatform.pojo.entity.JobInfo;
import com.kkirsii.recruitmentplatform.pojo.entity.SubmitState;
import com.kkirsii.recruitmentplatform.server.AdminJb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/admin/job")
public class AdminJobController {
    @Autowired
    AdminJb adminJb;

    @PostMapping("/publish")
    public Result publish(@RequestBody JobInfo jobInfo) {
        adminJb.publish(jobInfo);
        return Result.success("发布成功");
    }

    @DeleteMapping("/publish")
    public Result delete(@RequestParam("id") int id) {
        adminJb.delete(id);
        return Result.success("删除成功");
    }

    @PutMapping("/publish")
    public Result update(@RequestBody JobInfo jobInfo) {
        adminJb.update(jobInfo);
        return Result.success("更新成功");
    }

    //工作信息列表
    @GetMapping("/getlist")
    public Result getlist(@RequestParam("page") int page,@RequestParam("size") int size) {
        List<JobInfo> list=adminJb.getlist(page,size);
        return Result.success(list);
    }
    //投递信息列表
    @GetMapping("/submitlist")
    public Result submitlist(@RequestParam("page") int page,@RequestParam("size") int size) {
        List<SubmitState> list=adminJb.getSubmitList(page,size);
        return Result.success(list);
    }

    @PutMapping("/submitlist")
    public Result changeSubmitState(@RequestParam("id") int id,@RequestParam("state") int state) {
        boolean c=adminJb.changeSubmitState(id,state);
        if(c)
        {
            return Result.success();
        }
        return Result.error();
    }

    @PostMapping("/edit_jobinfo")
    public Result editJobInfo(@RequestBody JobInfo jobInfo) {
        adminJb.update(jobInfo);
        return Result.success("职位信息更新成功");
    }

}
