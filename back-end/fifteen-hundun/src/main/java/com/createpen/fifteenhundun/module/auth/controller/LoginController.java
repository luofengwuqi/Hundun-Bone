package com.createpen.fifteenhundun.module.auth.controller;

import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginEmailCodeDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginEmailPassWordDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginPhonePasswordDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginUsernamePasswordRequestDTO;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;
import com.createpen.fifteenhundun.module.auth.service.LoginService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user/login")
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }
    @PostMapping("/username-password")
    public Result<LoginRequestVO> loginUsernamePassword(@Valid @RequestBody LoginUsernamePasswordRequestDTO dto) {
        log.info("用户登录");
        log.info("已经接收到请求:LoginRequestDTO信息为{}",dto.toString());
        LoginRequestVO vo =  loginService.loginUsernamePassword(dto);
        return Result.success(vo);
    }

    @PostMapping("/phone-password")
    public Result<LoginRequestVO> loginPhonePassword(@RequestBody LoginPhonePasswordDTO dto)
    {
        LoginRequestVO  vo= loginService.loginPhonePassword(dto);
        log.info("手机密码登录成功");
        return Result.success(vo);
    }

    @PostMapping("/email-code")
    public Result<LoginRequestVO> loginEmailCode(@RequestBody LoginEmailCodeDTO dto)
    {
        LoginRequestVO  vo= loginService.loginEmailCode(dto);
        log.info("邮箱验证码登录成功");
        return Result.success(vo);
    }
    //和上一个方法配套的验证码发送方法
    @PostMapping("/email-code/send")
    public Result<Void> sendEmailCode(@RequestBody String email)
    {
        log.info("开始发送邮箱验证码");
        email= email.substring(10,email.length()-2);
        log.info("接收到的邮箱为{}",email);
        loginService.sendEmailCode(email);
        return Result.success();
    }

    //邮箱+验证码登录
    @PostMapping("/email-password")
    public Result<LoginRequestVO> loginEmailCode(@RequestBody LoginEmailPassWordDTO dto)
    {
        LoginRequestVO  vo= loginService.loginEmailPassword(dto);
        log.info("邮箱验证码登录成功");
        return Result.success(vo);
    }

    //登出
    @PostMapping("/logout")
    public Result<Void> logout(){
        log.info("开始登出");
        loginService.logout();
        return Result.success();
    }
}
