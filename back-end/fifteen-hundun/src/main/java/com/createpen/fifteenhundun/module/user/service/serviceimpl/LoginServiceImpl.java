package com.createpen.fifteenhundun.module.user.service.serviceimpl;

import com.createpen.fifteenhundun.common.exception.MyException.LoginException;
import com.createpen.fifteenhundun.common.exception.MyException.PasswordException;
import com.createpen.fifteenhundun.common.exception.MyException.UserExistedException;
import com.createpen.fifteenhundun.common.exception.MyException.UserNotFoundException;
import com.createpen.fifteenhundun.common.util.JwtUtil;
import com.createpen.fifteenhundun.common.util.PasswordUtil;
import com.createpen.fifteenhundun.module.user.mapper.UserMapper;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginPhonePasswordDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginUsernamePasswordRequestDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;
import com.createpen.fifteenhundun.module.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {
    private final UserMapper userMapper;

    public LoginServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public LoginRequestVO loginPhonePassword(LoginPhonePasswordDTO dto) {
        //检验存在性
        User u = userMapper.selectUserByPhone(dto.getPhone());
        if( u==null ){
            throw new UserExistedException("貌似没有这个手机号的...");
        }
        //校验密码
        if(!PasswordUtil.check(dto.getPassword(),u.getPasswordHash())){
            throw new PasswordException("密码输错了...");
        }
        //创建token
        String token = JwtUtil.makeToken(Math.toIntExact(u.getId()),u.getUsername());
        //塞进vo
        return new LoginRequestVO(token,u);
    }

    @Override
    public LoginRequestVO loginUsernamePassword(LoginUsernamePasswordRequestDTO l) {

        User u = getValidUserByUserName(l.getUsername());
        //校验是否账号被禁用
        log.info("开始校验用户是否被禁用");
        int status = u.getStatus();
        log.info("用户状态为:{}",status);
        if(u.getStatus()<=0)
        {
            log.info("用户被禁用");
            throw new LoginException("你的账户被ban了(#`Д´)ﾉ!(tell me why)");
        }
        //校验密码
        String password = l.getPassword() ;
        //逻辑有误 增补:
        log.info("已经查询到当前登录而存在的用户信息:{}",u);
        log.info("即将进行密码校验 传递密码:{},查询到的加密密码:{}",password,u.getPasswordHash());
        if (!PasswordUtil.check(password,u.getPasswordHash())) {
            throw new LoginException("密码错误");
        }
        log.info("已经通过密码校验");
        LoginRequestVO r = new LoginRequestVO();
        //生成token往token里面塞点信息
        Map<String,Object> tokenDatamap = new HashMap<>();
        tokenDatamap.put("id",u.getId());
        tokenDatamap.put("username",u.getUsername());
        //处理vo
        r.setToken(JwtUtil.makeToken(tokenDatamap));
        r.setUser(u);

        //设置loginTime
        u.setLastLoginAt(LocalDateTime.now());
        userMapper.updateUserByUserName(u);

        return r;

    }
    public User getValidUserByUserName(String username)
    {
        log.info("单个用户校验存在性开始");
        User u = userMapper.getUserByUserName(username);
        if(u==null)
        {
            throw new UserNotFoundException("用户不存在");
        }
        log.info("已经查询到当前使用而存在的用户信息:{}",u);
        return u;
    }
}
