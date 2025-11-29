package com.createpen.fifteenhundun.module.user.service.serviceimpl;

import com.createpen.fifteenhundun.common.util.JwtUtil;
import com.createpen.fifteenhundun.common.util.PasswordUtil;
import com.createpen.fifteenhundun.module.user.mapper.UserMapper;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginRequestDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;
import com.createpen.fifteenhundun.module.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper um;


    @Override
    public LoginRequestVO login(LoginRequestDTO l) {

        User u = um.selectByUserName(l.getUsername());
        if(u==null)
        {
            throw new RuntimeException("用户不存在");
        }
        //校验密码
        String password = l.getPassword() ;
        String addedMipassword = PasswordUtil.addMi(l.getPassword());
        l.setPassword(addedMipassword);
        if (!PasswordUtil.check(password, addedMipassword)) {
            throw new RuntimeException("密码错误");
        }

        LoginRequestVO r = new LoginRequestVO();
        //生成token往token里面塞点信息
        Map<String,Object> tokenDatamap = new HashMap<>();
        tokenDatamap.put("id",u.getId());
        tokenDatamap.put("username",u.getUsername());

        r.setToken(JwtUtil.makeToken(tokenDatamap));
        r.setUsername(u.getUsername());
        return r;

    }
}
