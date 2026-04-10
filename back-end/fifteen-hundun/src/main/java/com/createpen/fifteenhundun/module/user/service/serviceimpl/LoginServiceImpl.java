package com.createpen.fifteenhundun.module.user.service.serviceimpl;

import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.common.exception.MyException.*;
import com.createpen.fifteenhundun.common.util.EmailCodeUtil;
import com.createpen.fifteenhundun.common.util.JwtUtil;
import com.createpen.fifteenhundun.common.util.PasswordUtil;
import com.createpen.fifteenhundun.common.util.Redis.UserRedisUtil;
import com.createpen.fifteenhundun.module.user.mapper.UserMapper;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginEmailCodeDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginEmailPassWordDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginPhonePasswordDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginUsernamePasswordRequestDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;
import com.createpen.fifteenhundun.module.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.createpen.fifteenhundun.common.constant.RedisConstant.*;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserRedisUtil userRedisUtil;

    private final RedisTemplate<String, Object> redisTemplate;

    private final UserMapper userMapper;

    public LoginServiceImpl(UserMapper userMapper, RedisTemplate<String, Object> redisTemplate) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public LoginRequestVO loginPhonePassword(LoginPhonePasswordDTO dto) {
        //检验存在性
        User u = userMapper.selectUserByPhone(dto.getPhone());
        if( u== null ){
            throw new UserNotFoundException("貌似没有这个手机号的...");
        }
        //校验密码
        if(!PasswordUtil.check(dto.getPassword(),u.getPasswordHash())){
            throw new PasswordException("密码输错了...");
        }
        //创建token
        String token = JwtUtil.makeToken(Math.toIntExact(u.getId()),u.getUsername());
        saveTokenInRedis(token,u.getId());
        //4 存进缓存
        saveUserInRedis(u);
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
        if (!PasswordUtil.check(password,u.getPasswordHash())) {
            throw new LoginException("密码错误");
        }
        log.info("已经通过密码校验");
        //设置loginTime
        u.setLastLoginAt(LocalDateTime.now());
        userMapper.updateUserByUserName(u);
        //生成token往token里面塞点信息
        Map<String,Object> tokenDatamap = new HashMap<>();
        tokenDatamap.put("id",u.getId());
        tokenDatamap.put("username",u.getUsername());
        //生成token 并存入redis(方便过期)
        String token = JwtUtil.makeToken(tokenDatamap);
        saveTokenInRedis(token,u.getId());
        //处理vo
        LoginRequestVO vo = new LoginRequestVO();
        vo.setToken(token);
        vo.setUser(u);
        //存进缓存
        saveUserInRedis(u);
        return vo;

    }

    @Override
    public LoginRequestVO loginEmailCode(LoginEmailCodeDTO dto) {
        //1查询是否存在
        User u = userMapper.selectUserByEmail(dto.getEmail());
        if(u==null){
            throw new UserNotFoundException("没找到这个邮箱...");
        }
        //2校验验证码
        String trueCode = (String) redisTemplate.opsForValue().get(EMAIL_CODE_PATH+dto.getEmail());
        if(trueCode == null){
            throw new RuntimeException("验证码似乎过期了...");
        }
        if (!trueCode.equals(dto.getCode())) {
            throw new PasswordException("验证码不对的...");
        }
        //3通过 生成token 删除redis存的验证码 封装vo返回
        String token = JwtUtil.makeToken(Math.toIntExact(u.getId()),u.getUsername());
        saveTokenInRedis(token,u.getId());
//        删除redis里面的验证码
        redisTemplate.delete(EMAIL_CODE_PATH+dto.getEmail());
        LoginRequestVO vo = new LoginRequestVO();
        vo.setToken(token);
        vo.setUser(u);
        //4 存进缓存
        saveUserInRedis(u);
        return vo;
    }

    @Override
    public void sendEmailCode(String email) {
        //1校验邮箱是否存在
        User u = userMapper.selectUserByEmail(email);
        if(u==null){
            throw new UserNotFoundException("邮箱不存在...");
        }
        //2生成验证码并把他塞进redis
        String code = EmailCodeUtil.generateCode();
        //3发送验证码
        boolean result =  EmailCodeUtil.sendEmailCode(email,code);
        if(!result){
            throw new SendCodeException("验证码发送失败了QAQ(疑似并不是程序的问题...)");
        }
        //4执行无误 塞进redis
        redisTemplate.opsForValue().set(EMAIL_CODE_PATH+email,code,EMAIL_CODE_EXPIRE_TIME,TimeUnit.MINUTES);

    }

    @Override
    public LoginRequestVO loginEmailPassword(LoginEmailPassWordDTO dto) {
        //1校验邮箱是否存在
        User u = userMapper.selectUserByEmail(dto.getEmail());
        if(u==null){
            throw new UserNotFoundException("邮箱不存在...");
        }
        //2校验密码
        if(!PasswordUtil.check(dto.getPassword(),u.getPasswordHash())){
            throw new PasswordException("密码输错了...");
        }
        //3通过 封装vo返回
        String token = JwtUtil.makeToken(Math.toIntExact(u.getId()),u.getUsername());
        saveTokenInRedis(token,u.getId());
        LoginRequestVO vo = new LoginRequestVO();
        vo.setToken(token);
        vo.setUser(u);
        //4 存进缓存
        saveUserInRedis(u);
        //5 返回
        return vo;
    }

    @Override
    public void logout() {
        Long currentId = BaseContext.getCurrentId();
        userRedisUtil.clearUserToken(currentId);
        userRedisUtil.clearUserdata(currentId);
    }

    //共用方法：在 redis 中存储 token
    public void saveTokenInRedis(String token,Long userId)
    {
        log.info("开始在redis存储token:{}",token);
        redisTemplate.opsForValue().set(TOKEN_PATH+userId,token,TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    //辅助方法 校验用户存在性userService携带过来的...
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
    //辅助方法 把登录的用户对象塞进redis
    public void saveUserInRedis(User u){
        redisTemplate.opsForValue().set(USER_DATA_PATH+u.getId(),u,TOKEN_EXPIRE_TIME,TimeUnit.MINUTES);
    }

}

