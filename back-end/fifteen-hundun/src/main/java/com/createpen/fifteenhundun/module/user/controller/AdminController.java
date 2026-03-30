package com.createpen.fifteenhundun.module.user.controller;

import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.user.pojo.DTO.*;
import com.createpen.fifteenhundun.module.user.pojo.VO.PageVO;
import com.createpen.fifteenhundun.module.user.service.AdminService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
//@RequestMapping("/admins")
@RequestMapping
public class AdminController {
    @Autowired
    private AdminService as;


    //新建管理员
    @PostMapping("/admins")
    public Result addAdmin(@RequestBody AddAdminDTO dto){
        log.info("新建管理员");
        as.addAdmin(dto);
        return Result.success();
    }

    //统计用户数量
    @GetMapping("/users/count")
    public Result<Integer> getUserNumber()
    {
        log.info("统计用户数量");
        Integer num = as.getUserNumber();
        return Result.success(num);
    }
    //生成邀请码
    @GetMapping("/admin/invite-code")
    public Result<String> makeInviteCode()
    {
        log.info("生成邀请码");
        String code = as.makeInviteCode();
        return Result.success(code);
    }
    //禁用或启用用户
    @PutMapping("/user/status")
    public Result<Integer> changeStatus(@RequestParam("username") String username)
    {
        log.info("禁用或启用用户");
        Integer status = as.changeStatus(username);
        return Result.success(status);
    }
    //删除用户
//    @DeleteMapping("/admins/users")
//    public Result deleteUser(@RequestBody DeleteUsersDTO dto)
//    {
//        //todoed:优化这里的删除逻辑 改为批量删除 而且这里不符合RESTful风格 好麻烦(遗憾离场)
//        log.info("删除用户");
//        as.deleteUserByUserName(username);
//
//        return Result.success();
//    }

    //分页查询用户

    //分页查询日志
    @GetMapping("/logs")
    public Result SelectLog(@Valid @RequestBody GetLogsDTO dto)
    {
        log.info("日志分页查询");
        PageVO vo = as.getLogs(dto);
        return Result.success(vo);
    }


}
