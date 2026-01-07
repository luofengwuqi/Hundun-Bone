package com.createpen.fifteenhundun.module.user.controller;

import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.user.pojo.DTO.DeleteUsersDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginRequestDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.RegisterDTO;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;
import com.createpen.fifteenhundun.module.user.pojo.VO.RegisterVO;
import com.createpen.fifteenhundun.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@Slf4j
public class UserController {

    @Autowired
    private UserService us;

    //auth代表认证资源 至于认证资源是啥我不知道
    @PostMapping("/auth/login")
    public Result login(@Valid @RequestBody LoginRequestDTO l) {
        log.info("用户登录");
        log.info("已经接收到请求:LoginRequestDTO信息为{}",l.toString());
        LoginRequestVO result =  us.login(l);
//        if(result == null)
//        {
//            return Result.fail("用户名或密码错误");
//        }
        return Result.success(result);
    }

    @PostMapping("/auth/users")
    public Result register(@Valid @RequestBody RegisterDTO dto)
    {
        log.info("用户注册");
        log.info("已经接收到请求:RegisterDTO信息为{}",dto.toString());
        RegisterVO r = us.register(dto);
        return Result.success(r);
    }

    //todoed:优化这里的删除逻辑 改为批量删除
    @DeleteMapping("/users")
    public Result deleteUser(@Valid @RequestBody DeleteUsersDTO dto) throws Exception {
        log.info("用户删除");
//        log.info("即将进行用户删除 捕获到信息:{}",dto);
        us.deleteUser(dto);
        return Result.success();
    }
}
