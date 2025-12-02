package com.createpen.fifteenhundun.module.user.service.serviceimpl;

import com.createpen.fifteenhundun.common.util.JwtUtil;
import com.createpen.fifteenhundun.common.util.PasswordUtil;
import com.createpen.fifteenhundun.module.user.mapper.UserMapper;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginRequestDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.RegisterDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;
import com.createpen.fifteenhundun.module.user.pojo.VO.RegisterVO;
import com.createpen.fifteenhundun.module.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper um;



    @Transactional
    @Override
    public LoginRequestVO login(LoginRequestDTO l) {

        User u = um.selectByUserName(l.getUsername());
        if(u==null)
        {
            throw new RuntimeException("用户不存在");
        }//已通过测试
        //校验密码
        String password = l.getPassword() ;
        //逻辑有误 增补:
        log.info("已经查询到当前登录而存在的用户信息:{}",u.toString());
        log.info("即将进行密码校验 传递密码:{},查询到的加密密码:{}",password,u.getPasswordHash());
        //boolean result = PasswordUtil.check("15hundun", "$2a$12$ZYFpx2DwkrPbaHaBJPrMQe1XEl8bmz4N0Z0XdOp/vRVez81ZEP0Pa");
        //System.out.println("密码校验结果: " + result);
        if (!PasswordUtil.check(password,u.getPasswordHash())) {
            throw new RuntimeException("密码错误");
        }
        log.info("已经通过密码校验");
        LoginRequestVO r = new LoginRequestVO();
        //生成token往token里面塞点信息
        Map<String,Object> tokenDatamap = new HashMap<>();
        tokenDatamap.put("id",u.getId());
        tokenDatamap.put("username",u.getUsername());

        r.setToken(JwtUtil.makeToken(tokenDatamap));
        r.setUsername(u.getUsername());

        //设置loginTime
        u.setLastLoginAt(LocalDateTime.now());
        um.updateUserByUserName(u);

        return r;

    }


    @Transactional
    @Override
    public RegisterVO register(RegisterDTO dto) {
        if(!dto.getPassword().equals(dto.getCheckPassword()))
        {
            throw new RuntimeException("两次输入的密码不一致");
        }


        //判断是否已经存在了这个user
        User uExisted = um.selectByUserName(dto.getUsername());
        if(uExisted!=null)
        {
            throw new RuntimeException("用户名已被使用");
        }
        User u = new User();
        BeanUtils.copyProperties(dto,u);

        u.setPasswordHash(PasswordUtil.addMi(dto.getPassword()));
        um.insertUser(u);

        return new RegisterVO(u.getUsername());
    }
}
