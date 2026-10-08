package com.createpen.fifteenhundun.module.admin.controller;

import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.admin.pojo.DTO.AddAdminDTO;
import com.createpen.fifteenhundun.module.admin.pojo.DTO.GetUsersDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.GetLogsDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.UpdateUserDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.pojo.VO.PageVO;
import com.createpen.fifteenhundun.module.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    //新建管理员
    @PostMapping("/admin")
    public Result<Void> addAdmin(@RequestBody AddAdminDTO dto){
        log.info("新建管理员");
        adminService.addAdmin(dto);
        return Result.success();
    }

    //统计用户数量(未使用)
    @GetMapping("/admin/users/count")
    public Result<Integer> getUserNumber()
    {
        log.info("统计用户数量");
        Integer num = adminService.getUserNumber();
        return Result.success(num);
    }
    //生成邀请码
    @GetMapping("/admin/invite-code")
    public Result<String> makeInviteCode()
    {
        log.info("生成邀请码");
        String code = adminService.makeInviteCode();
        return Result.success(code);
    }
    //修改用户信息
    @PutMapping("/user")
    public Result<Void> updateUser(@RequestBody UpdateUserDTO dto)
    {
        log.info("修改用户信息{}",dto);
        adminService.updateUser(dto);
        return Result.success();
    }
    //禁用或启用用户
    @PutMapping("/admin/user/status")
    public Result<Integer> changeStatus(@RequestParam("username") String username)
    {
        log.info("禁用或启用用户");
        Integer status = adminService.changeStatus(username);
        return Result.success(status);
    }
    @DeleteMapping("/admin/user/{id}")
    public Result<Void> deleteUser(@PathVariable Long id)
    {
        log.info("删除单个用户 前端传参id:{}",id);
        adminService.deleteUserById(id);

        return Result.success();
    }
    //分页查询日志
    @GetMapping("/logs")
    public Result<PageVO> SelectLog(@Valid @RequestBody GetLogsDTO dto)
    {
        log.info("日志分页查询");
        PageVO vo = adminService.getLogs(dto);
        return Result.success(vo);
    }
    //todoed:优化这里的删除逻辑 改为批量删除
    //26/3/26 芜柒:看见之前的代码想把自己打一顿怎么办
    //26/4/1  芜柒:腿打断

    //分页查询用户
    @PostMapping("/user/page")
    public Result<PageVO<User>> getUsers(@Valid @RequestBody GetUsersDTO dto)
    {
        log.info("{}",dto);
        //page:当前的页数
        //size:前端指定的每页的条数
        log.info("分页查询用户");
        PageVO<User> vo = adminService.getUsers(dto);
        return Result.success(vo);
    }

    @GetMapping("/admin/user/info")
    public Result<User> getUserInfoById(@RequestParam(required = false) Long id){
        log.info("获取用户信息");
        User user = adminService.getUserInfoById(id);
        return Result.success(user);
    }

}
