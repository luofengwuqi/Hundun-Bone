package com.createpen.fifteenhundun.module.user.controller;

import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.user.pojo.VO.PageVO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.AddAdminDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.GetLogsDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.GetUsersDTO;
import com.createpen.fifteenhundun.module.user.service.AdminService;
import jakarta.validation.Valid;
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
    public Result addAdmin(@RequestBody AddAdminDTO dto){
        log.info("新建管理员");
        as.addAdmin(dto);
        return Result.success();
    }

    //统计用户数量
    @GetMapping("/count")
    public Result<Integer> getUserNumber()
    {
        log.info("统计用户数量");
        Integer num = as.getUserNumber();
        return Result.success(num);
    }
    //生成邀请码
    @GetMapping("/makeInviteCode")
    public Result makeInviteCode()
    {
        log.info("生成邀请码");
        String code = as.makeInviteCode();
        return Result.success(code);
    }
    //禁用或启用用户
    @GetMapping("/changeStatus")
    public Result changeStatus(@RequestParam("username") String username)
    {
        log.info("禁用或启用用户");
        as.changeStatus(username);
        return Result.success();
    }
    //删除用户
    @GetMapping("/deleteUser")
    public Result deleteUser(@RequestParam("username") String username)
    {
        log.info("删除用户");
        as.deleteUserByUserName(username);
        return Result.success();
    }

    //分页查询用户
    @PostMapping("/getUsers")
    public Result getUsers(@Valid @RequestBody GetUsersDTO dto)
    {
        //page:当前的页数
        //size:前端指定的每页的条数
        log.info("分页查询用户");
        PageVO vo = as.getUsers(dto);
        return Result.success(vo);
    }
    //分页查询日志
    @PostMapping("/getLogs")
    public Result SelectLog(@Valid @RequestBody GetLogsDTO dto)
    {
        log.info("日志分页查询");
        PageVO vo = as.getLogs(dto);
        return Result.success(vo);
    }


}
