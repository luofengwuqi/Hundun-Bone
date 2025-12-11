package com.createpen.fifteenhundun.module.user.service.serviceimpl;

import com.createpen.fifteenhundun.common.constant.ImportantConstant;
import com.createpen.fifteenhundun.common.exception.MyException.InsufficientPermissionsException;
import com.createpen.fifteenhundun.common.exception.MyException.LoginException;
import com.createpen.fifteenhundun.common.exception.MyException.UserExistedException;
import com.createpen.fifteenhundun.common.exception.MyException.UserNotFoundException;
import com.createpen.fifteenhundun.common.util.PasswordUtil;
import com.createpen.fifteenhundun.module.user.mapper.AdminMapper;
import com.createpen.fifteenhundun.module.user.mapper.UserMapper;
import com.createpen.fifteenhundun.module.user.pojo.DTO.addAdminDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper am;
    @Autowired
    private UserMapper um;
    @Autowired
    private UserServiceImpl us;

    @Override
    public Integer getUserNumber() {
        return am.getUserNumber();
    }

    @Transactional
    @Override
    public void changeStatus(String username) {
        User u = um.selectByUserName( username);
        if(u== null)
        {
            throw new UserNotFoundException("用户不存在");
        }
        else if(u.getUsername().equals(ImportantConstant.ADMINNAME))
        {
            throw new InsufficientPermissionsException("你想干啥 不许你这样对管理员大人");
        }
        if(u.getStatus()==1)
        {
            u.setStatus(0);
        }
        else
        {
            u.setStatus(1);
        }
        um.updateUserByUserName(u);
        log.info("用户状态已修改");
        log.info("用户状态:{}",u.getStatus());
    }

    @Override
    public void addAdmin(addAdminDTO dto) {
        log.info("开始添加管理员");
        log.info("开始校验是否已经存在");
        //这里的逻辑应该只有当时写代码的我才能看得懂 这里就当是一个教训
        try{
            us.isUserExisted(dto.getUsername()).getUsername();
        }catch (UserNotFoundException e)//不存在才继续 真拧巴
        {
            log.info("开始校验重要密码");
            if (!PasswordUtil.check(dto.getImportantPassword(),ImportantConstant.IMPORTANTPASSWORD)) {
                throw new LoginException("你貌似没有权限 你干嘛啊??");
            }

            User u = new User();
            u.setUsername(dto.getUsername());
            u.setPasswordHash(PasswordUtil.addMi(dto.getPassword()));
            u.setStatus(2);
            u.setEmail(dto.getEmail());
            u.setPhone(dto.getPhone());
            u.setAvatarUrl(dto.getAvatarUrl());
            u.setLastLoginAt(LocalDateTime.now());

            um.insertUser(u);
            return;
        }
        throw new UserExistedException("用户已存在");


    }


}
