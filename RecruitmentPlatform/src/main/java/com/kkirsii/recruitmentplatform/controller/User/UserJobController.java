package com.kkirsii.recruitmentplatform.controller.User;

import com.kkirsii.recruitmentplatform.mapper.SubmitStateMapper;
import com.kkirsii.recruitmentplatform.pojo.VO.Result;
import com.kkirsii.recruitmentplatform.pojo.entity.JobInfo;
import com.kkirsii.recruitmentplatform.pojo.entity.SubmitState;
import com.kkirsii.recruitmentplatform.server.UserJb;
import com.kkirsii.recruitmentplatform.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
import java.util.List;

@RestController
@RequestMapping("/user/job")
public class UserJobController {

    private static final Logger log = LoggerFactory.getLogger(UserJobController.class);
    @Autowired
    private UserJb userJb;

    @Autowired
    private SubmitStateMapper submitStateMapper;

    @GetMapping("/getlist")
    public Result getUserList(@RequestParam("page") int page,@RequestParam("size") int size) {
        log.info("获取职位列表, 页码: " + page + ", 每页大小: " + size);
        List<JobInfo>list= userJb.getlist(page,size);
        return Result.success(list);

    }
    @PostMapping("/submit")
    public Result submit(@RequestParam("id") int id) {
        log.info("投递职位 ID: " + id);
        boolean c= userJb.submit(id);
        if(c)
        {
            return Result.success();
        }
        else
        {
            return Result.error();
        }
    }

    @PutMapping("/sumit_cancel")
    public Result submit_cancel(@RequestParam("id") int id) {
        log.info("取消投递职位 ID: " + id);
        submitStateMapper.updateState(UserContext.getEmail(), id, 5);
        return Result.success();
    }

    @GetMapping("/get_submitState")
    public Result get_submitState() {
        log.info("获取投递状态");
        List<SubmitState> list=submitStateMapper.selectByEmail(UserContext.getEmail());
        return Result.success(list);
    }

}
