package com.createpen.fifteenhundun.module.user.controller;

import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.user.pojo.DTO.addAdminDTO;
import com.createpen.fifteenhundun.module.user.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user/admin")
public class AdminController {
    @Autowired
    private AdminService as;


    //新建管理员
    @PostMapping("/add")
    public Result addAdmin(@RequestBody addAdminDTO dto){
        log.info("新建管理员");
        as.addAdmin(dto);
        return Result.success();
    }

    //    统计数据部分
    @GetMapping("/count")
    public Result<Integer> getUserNumber()
    {
        log.info("统计用户数量");
        Integer num = as.getUserNumber();
        return Result.success(num);
    }
    //禁用或启用用户
    @GetMapping("/changeStatus")
    public Result changeStatus(@RequestParam("username") String username)
    {
        log.info("禁用或启用用户");
        as.changeStatus(username);
        return Result.success();
    }

}
