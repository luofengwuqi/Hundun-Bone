package com.createpen.fifteenhundun.module.user.service.serviceimpl;

import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.common.exception.MyException.*;
import com.createpen.fifteenhundun.common.util.PasswordUtil;
import com.createpen.fifteenhundun.common.util.UserRedisUtil;
import com.createpen.fifteenhundun.module.user.mapper.UserMapper;
import com.createpen.fifteenhundun.module.user.pojo.DTO.DeleteUsersDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.RegisterDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.pojo.VO.RegisterVO;
import com.createpen.fifteenhundun.module.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    //26/3/26 芜柒:看见之前的代码想把自己打一顿怎么办
    //26/4/1  芜柒:腿打断
    @Autowired
    private UserMapper um;
    @Autowired
    private UserRedisUtil userRedisUtil;
    @Transactional(rollbackFor = Exception.class)
    public RegisterVO register(RegisterDTO dto) {
        //1检验用户是否已经存在
        checkUserNotExists(dto.getUsername());
        //2 不存在 插入
        User u = new User();
        BeanUtils.copyProperties(dto,u);
        u.setPasswordHash(PasswordUtil.addMi(dto.getPassword()));
        um.insertUser(u);
        //3 删除管理端用户查询的分页缓存
        userRedisUtil.clearUsersCache();
        //4 返回
        return new RegisterVO(u.getUsername());
    }

    @Transactional
    @Override
    public void deleteUsers(DeleteUsersDTO dto) throws IllegalAccessException {

        //拿到执行人对象
        String operateUsername = BaseContext.getCurrentUsername();
        User operateUser = getValidUserByUserName(operateUsername);
        //检验执行者身份
        log.info("当前操作用户{}状态为{}", operateUser.getUsername(), operateUser.getStatus());
        if (operateUser.getStatus() == 1) {
            //普通用户 走根据密码删除
            //1 校验密码
            if (!PasswordUtil.check(dto.getPassword(), operateUser.getPasswordHash())) {
                throw new PasswordException("密码不正确!");
            }
            //2 校验传参合法性
            if (dto.getUsernames().size() > 1 || !dto.getUsernames().get(0).equals(operateUsername)) {//删除自己之外的用户则报错
                throw new InsufficientPermissionsException("当前用户权限不足!");
            }


            //3删除自己

            um.deleteUserByUserNameList(dto.getUsernames());

        } else if (operateUser.getStatus() == 2 || operateUser.getStatus() == 3) {
            //管理员 无需密码删除
            //1 传参存在性判断
            getValidUserListByUserNameList(dto.getUsernames());
            //2 删除这些合法参数
            um.deleteUserByUserNameList(dto.getUsernames());
        } else {
            throw new IllegalAccessException("用户已被禁用!");
        }
    }

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

    public User selectUserById(Long id) {
        return um.selectUserById(id);
    }

}


