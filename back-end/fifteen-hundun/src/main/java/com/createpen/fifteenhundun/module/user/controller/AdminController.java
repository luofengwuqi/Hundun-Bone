package com.createpen.fifteenhundun.module.user.controller;

import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.user.pojo.DTO.PageUsersVO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.addAdminDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.getUsersDTO;
import com.createpen.fifteenhundun.module.user.service.AdminService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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

    //分页查询用户
    @PostMapping("/getUsers")
    public Result getUsers(@Valid @RequestBody getUsersDTO dto)
    {
        //page:当前的页数
        //size:前端指定的每页的条数
        log.info("分页查询用户");
        PageUsersVO vo = as.getUsers(dto);
        return Result.success(vo);
    }

}
