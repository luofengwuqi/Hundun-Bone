package com.createpen.fifteenhundun.module.user.controller;

import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginPhonePasswordDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginUsernamePasswordRequestDTO;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;
import com.createpen.fifteenhundun.module.user.service.LoginService;
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
}
