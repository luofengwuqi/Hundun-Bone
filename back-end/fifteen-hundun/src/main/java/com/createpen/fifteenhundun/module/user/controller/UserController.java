package com.createpen.fifteenhundun.module.user.controller;

import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.user.pojo.DTO.DeleteUsersDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.GetUsersDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.RegisterDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.pojo.VO.PageVO;
import com.createpen.fifteenhundun.module.user.pojo.VO.RegisterVO;
import com.createpen.fifteenhundun.module.user.service.AdminService;
import com.createpen.fifteenhundun.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService us;
    private final AdminService as;

    public UserController(UserService us, AdminService as) {
        this.us = us;
        this.as = as;
    }

    @PostMapping("/register")
    public Result<RegisterVO> register(@Valid @RequestBody RegisterDTO dto)
    {
        log.info("用户注册");
        log.info("已经接收到请求:RegisterDTO信息为{}",dto.toString());
        RegisterVO r = us.register(dto);
        return Result.success(r);
    }

    //todoed:优化这里的删除逻辑 改为批量删除
    //26/3/26 芜柒:看见之前的代码想把自己打一顿怎么办
    //26/4/1  芜柒:腿打断
    @PostMapping("/page")
    //@Cacheable(cacheNames = "usersCache" ,key = "#dto.page + '_' + #dto.size + '_' + #dto.name + '_' + #dto.status")
    public Result<PageVO<User>> getUsers(@Valid @RequestBody GetUsersDTO dto)
    {
        log.info("{}",dto);
        //page:当前的页数
        //size:前端指定的每页的条数
        log.info("分页查询用户");
        PageVO<User> vo = as.getUsers(dto);
        return Result.success(vo);
    }

    @DeleteMapping
    public Result<Void> deleteUsers(@Valid @RequestBody DeleteUsersDTO dto) throws Exception {
        log.info("用户删除");
//        log.info("即将进行用户删除 捕获到信息:{}",dto);
        us.deleteUsers(dto);
        return Result.success();
    }

    //上传用户头像的接口
    @PutMapping("/avatar")
    public Result<Void> updateAvatar(@RequestParam("file") MultipartFile file) {
        log.info("上传用户头像");
        us.updateAvatar(file);
        return Result.success();
    }

    @GetMapping("/info")
    public Result<User> getUserInfo() {
        log.info("获取用户信息");
        User u = us.selectUserById(BaseContext.getCurrentId());
        return Result.success(u);
    }

}
