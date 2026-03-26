package com.createpen.fifteenhundun.module.user.service.serviceimpl;

import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.common.exception.MyException.*;
import com.createpen.fifteenhundun.common.util.JwtUtil;
import com.createpen.fifteenhundun.common.util.PasswordUtil;
import com.createpen.fifteenhundun.module.user.mapper.UserMapper;
import com.createpen.fifteenhundun.module.user.pojo.DTO.DeleteUsersDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginUsernamePasswordRequestDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.RegisterDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;
import com.createpen.fifteenhundun.module.user.pojo.VO.RegisterVO;
import com.createpen.fifteenhundun.module.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    //26/3/26 芜柒:看见之前的代码想把自己打一顿怎么办

    @Autowired
    private UserMapper um;
//    @Autowired
//    private RedisCacheManager cacheManager;

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
        //boolean result = PasswordUtil.check("15hundun", "$2a$12$ZYFpx2DwkrPbaHaBJPrMQe1XEl8bmz4N0Z0XdOp/vRVez81ZEP0Pa");
        //System.out.println("密码校验结果: " + result);
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
//        log.info("debug:#{}",u);
        um.updateUserByUserName(u);
        //todoed:测试sql拦截器 测试通过
//        String currentSql= CurrentSqlInterceptor.getCurrentSql();
//        log.info("当前sql:{}",currentSql);
//        CurrentSqlInterceptor.clear();

        return r;

    }


    @Transactional(rollbackFor = Exception.class)
//    @CacheEvict(cacheNames = "usersCache" ,allEntries = true)
//    @Cacheable(cacheNames = "userCache",key = "#dto.username")
    public RegisterVO register(RegisterDTO dto) {
//        if(!dto.getPassword().equals(dto.getCheckPassword()))
//        {
//            throw new LoginException("两次输入的密码不一致");
//        }
        //判断是否已经存在了这个user
//        User uExisted = um.selectByUserName(dto.getUsername());
//        if(uExisted!=null)
//        {
//            throw new UserExistedException("用户名已被使用");
//        }
//        User  uExisted = getValidUserByUserName(dto.getUsername());
        //1检验用户是否已经存在
        checkUserNotExists(dto.getUsername());
        //2 不存在 插入
        User u = new User();
        BeanUtils.copyProperties(dto,u);
        u.setPasswordHash(PasswordUtil.addMi(dto.getPassword()));
        um.insertUser(u);
        //3 手动插入缓存
//        cacheUser(u);
        //4 返回
        return new RegisterVO(u.getUsername());
    }

    @Transactional
    @Caching(
        evict={
            @CacheEvict(cacheNames = "usersCache" ,allEntries = true),
            @CacheEvict(cacheNames = "userCache" ,allEntries = true)
        }
    )
    @Override
    public void deleteUsers(DeleteUsersDTO dto) throws IllegalAccessException {

        //拿到执行人对象
        String operateUsername = BaseContext.getCurrentUsername();
        User operateUser = getValidUserByUserName(operateUsername);
        //检验执行者身份
        log.info("当前操作用户{}状态为{}",operateUser.getUsername(),operateUser.getStatus());
        if(operateUser.getStatus()==1)
        {
            //普通用户 走根据密码删除
            //1 校验密码
            if(!PasswordUtil.check(dto.getPassword(),operateUser.getPasswordHash())) {
                throw new PasswordException("密码不正确!");
            }
            //2 校验传参合法性
            if(dto.getUsernames().size()>1 || !dto.getUsernames().get(0).equals(operateUsername))
            {//删除自己之外的用户则报错
                throw new InsufficientPermissionsException("当前用户权限不足!");
            }


            //3删除自己

            um.deleteUserByUserNameList(dto.getUsernames());

        } else if (operateUser.getStatus()==2 || operateUser.getStatus()==3) {
            //管理员 无需密码删除
            //1 传参存在性判断
            getValidUserListByUserNameList(dto.getUsernames());
            //2 删除这些合法参数
            um.deleteUserByUserNameList(dto.getUsernames());
        }
        else{
            throw new IllegalAccessException("用户已被禁用!");
        }

//        checkPassWordByUserAndPassword(u,dto.getPassword());
        //核心业务
//        um.deleteUserByUserName(dto.getUsername());
//        log.info("已经删除用户:{}",dto.getUsername());
    }


//    @Override
//    public Long getIdByUsername(String username) {
//        User u = getUserInCache(username);
//        if(u==null)
//        {
//            u = getValidUserByUserName(username);
//            cacheUser(u);
//        }
//        return u.getId();
//    }


    //这里代码耦合太强 重整!
    //辅助方法过来User 帮忙校验密码
    //通过:无事发生
    //不通过:抛出RuntimeException("密码错误")
//    public void checkPassWordByUserAndPassword(User u,String password)
//    {
//
//        log.info("校验存在性");
//        User findedUser = isUserExisted(u.getUsername());
////        findedUser = um.selectByUserName(u.getUsername());
////        if (findedUser==null)
////        {
////            throw new UserNotFoundException("用户不存在");
////        }
////            log.info("截获用户不存在异常 准备抛给全局异常处理器");
//
//        //存在开始校验密码
//        log.info("正在校验密码");
//        if (!PasswordUtil.check(password,findedUser.getPasswordHash())) {
//            throw new LoginException("密码错误");
//        }
//    }
//    public User getUserInCache(String username)
//    {
//        Cache cache = cacheManager.getCache("userCache");
//        Cache.ValueWrapper valueWrapper = cache.get(username);
//        //没找到
//        if(valueWrapper==null)
//        {
//            log.info("缓存未命中,用户{}不在缓存中",username);
//            return null;
//        }
//        //找到了
//        return (User) valueWrapper.get();
//    }
    //共通方法:单个用户塞入缓存
//    public void cacheUser(User u)
//    {
//        Cache userCache = cacheManager.getCache("userCache");
//        if(userCache!=null)
//        {
//            log.info("缓存正在存入用户{}",u.getUsername());
//            userCache.put(u.getUsername(),u);
//            log.info("缓存成功存入用户{}",u.getUsername());
//        }
//        log.info("缓存中{}已存在",u.getUsername());
//    }
    //辅助性方法 用户存在报错
    public void checkUserNotExists(String username)
    {
        User u = um.getUserByUserName(username);
        if(u!=null)
        {
            throw new UserExistedException("用户已存在");
        }

    }

    //辅助性方法 用户是否存在
    //1 单个用户
    public User getValidUserByUserName(String username)
        {
            log.info("单个用户校验存在性开始");
            User u = um.getUserByUserName(username);
            if(u==null)
            {
                throw new UserNotFoundException("用户不存在");
            }
            log.info("已经查询到当前使用而存在的用户信息:{}",u);
            return u;
        }
    //2 用户名集合
    public List<User> getValidUserListByUserNameList(List<String> userNameList)
    {
        if(userNameList==null)
        {
            throw new IllegalArgumentException("删除用户传参错误") ;
        }
        log.info("多个用户校验存在性开始");
        List<User> userList = um.selectByUserNameList(userNameList);
        //num代表测试有没有不存在的用户名
        int num =  userNameList.size()-userList.size();
        if(num!=0)
        {
            log.warn("有"+num+"个不存在的用户 服务器已经忽视");
        }
//        log.error("通过");

        return userList;
    }

}
