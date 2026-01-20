package com.createpen.fifteenhundun.module.user.service.serviceimpl;

//import com.createpen.fifteenhundun.common.constant.ImportantConstant;
import com.createpen.fifteenhundun.common.exception.MyException.InsufficientPermissionsException;
import com.createpen.fifteenhundun.common.exception.MyException.LoginException;
import com.createpen.fifteenhundun.common.exception.MyException.UserExistedException;
import com.createpen.fifteenhundun.common.exception.MyException.UserNotFoundException;
import com.createpen.fifteenhundun.common.util.PasswordUtil;
import com.createpen.fifteenhundun.common.util.RandomCodeUtil;
import com.createpen.fifteenhundun.module.user.mapper.AdminMapper;
import com.createpen.fifteenhundun.module.user.mapper.UserMapper;
import com.createpen.fifteenhundun.module.user.pojo.DTO.GetLogsDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.OperateLog;
import com.createpen.fifteenhundun.module.user.pojo.VO.PageVO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.AddAdminDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.GetUsersDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.service.AdminService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
    public Integer changeStatus(String username) {
        User u = um.getUserByUserName( username);
        if(u== null)
        {
            throw new UserNotFoundException("用户不存在");
        }
//        else if(u.getUsername().equals(ImportantConstant.ADMINNAME))
//        {
//            throw new InsufficientPermissionsException("你想干啥 不许你这样对管理员大人");
//        }
        Integer status = u.getStatus();

        if(status ==1)
        {
            u.setStatus(0);
        }
        else if(status ==0)
        {
            u.setStatus(1);
        }
        else if(status == 2)
        {
            u.setStatus(-1);
        }
        else if(status == -1)
        {
            u.setStatus(2);
        }
        else if(status ==3)
        {
            throw new InsufficientPermissionsException("你想干啥 不许你这样对管理员大人");
        }

        um.updateUserByUserName(u);
        log.info("用户状态已修改");
        log.info("用户状态:{}", u.getStatus());
        return u.getStatus();
    }

    @Override
    public void addAdmin(AddAdminDTO dto) {
        log.info("开始添加管理员");
        log.info("开始校验是否已经存在");
        //这里的逻辑应该只有当时写代码的我还有上帝才能看得懂 这里就当是一个教训
        try{
            us.getValidUserByUserName(dto.getUsername()).getUsername();
        }catch (UserNotFoundException e)//不存在才继续 真拧巴
        {
            log.info("开始校验重要密码");
            String importantPassword = am.selectInvitePassword(dto.getImportantPassword());
            log.info("检索到的邀请码#{}",importantPassword);
            if(importantPassword==null){
                throw new LoginException("你貌似没有被邀请 你干嘛啊??");
            }
            log.info("重要密码已校验通过");
//            if (!PasswordUtil.check(dto.getImportantPassword(),ImportantConstant.IMPORTANTPASSWORD)) {
//                throw new LoginException("你貌似没有被邀请 你干嘛啊??");
//            }

            User u = new User();
            u.setUsername(dto.getUsername());
            u.setPasswordHash(PasswordUtil.addMi(dto.getPassword()));
            u.setStatus(2);
            u.setEmail(dto.getEmail());
            u.setPhone(dto.getPhone());
            u.setAvatarUrl(dto.getAvatarUrl());
            u.setLastLoginAt(LocalDateTime.now());

            um.deleteUserByUserName("管理员待注册"+importantPassword);
            um.insertUser(u);
            return;
        }
        throw new UserExistedException("用户已存在");


    }

    @Override
    public PageVO getUsers(GetUsersDTO dto) {
        log.info("开始分页查询用户");
        log.info("分页参数:{}",dto);
        PageHelper.startPage(dto.getPage(),dto.getSize());
        List<User> userList = am.getUsers(dto.getName(),dto.getStatus(),dto.getBeginTime(),dto.getEndTime(),dto.getLoginDays());
        Page<User> p = (Page<User>)userList;
        return new PageVO(p.getTotal(),p.getResult());
    }

    @Override
    public String makeInviteCode() {
        User u =new User();
        u.setPasswordHash(PasswordUtil.addMi("fqeqvbqeiovfbqiudbv"));
        u.setStatus(0);
        //todoed: 生成邀请码
        u.setInviteCode(RandomCodeUtil.makeComplexCode(10));
        u.setUsername("管理员待注册"+u.getInviteCode());
        um.insertUser(u);
        return u.getInviteCode();
    }

    @Override
    public void deleteUserByUserName(String username) {
        um.deleteUserByUserName(username);
    }

    @Override
    public PageVO getLogs(GetLogsDTO dto) {
        log.info("{}",dto);
        PageHelper.startPage(dto.getPage(),dto.getSize());
        List<OperateLog> l = am.getLogs(dto);
        Page p = (Page) l;

        return new PageVO(p.getTotal(),p.getResult());
    }


}
