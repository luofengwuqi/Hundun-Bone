package com.createpen.fifteenhundun.module.user.controller;

import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.user.pojo.DTO.DeleteDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginRequestDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.RegisterDTO;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;
import com.createpen.fifteenhundun.module.user.pojo.VO.RegisterVO;
import com.createpen.fifteenhundun.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService us;

    @PostMapping("/login")
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

    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterDTO dto)
    {
        log.info("用户注册");
        log.info("已经接收到请求:RegisterDTO信息为{}",dto.toString());
        RegisterVO r = us.register(dto);
        return Result.success(r);
    }

    @PostMapping("/delete")
    public Result deleteUser(@Valid @RequestBody DeleteDTO dto)
    {
        log.info("即将进行用户删除 捕获到信息:{}",dto);
        us.deleteUser(dto);
        return Result.success();
    }
}
