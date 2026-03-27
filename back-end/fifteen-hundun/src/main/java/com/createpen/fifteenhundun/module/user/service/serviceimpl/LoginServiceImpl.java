package com.createpen.fifteenhundun.module.user.service.serviceimpl;

import com.createpen.fifteenhundun.common.exception.MyException.PasswordException;
import com.createpen.fifteenhundun.common.exception.MyException.UserExistedException;
import com.createpen.fifteenhundun.common.util.JwtUtil;
import com.createpen.fifteenhundun.common.util.PasswordUtil;
import com.createpen.fifteenhundun.module.user.mapper.UserMapper;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginPhonePasswordDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;
import com.createpen.fifteenhundun.module.user.service.LoginService;
import org.springframework.stereotype.Service;


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
}
