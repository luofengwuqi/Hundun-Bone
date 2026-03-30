package com.createpen.fifteenhundun.module.user.service.serviceimpl;

//import com.createpen.fifteenhundun.common.constant.ImportantConstant;
import com.createpen.fifteenhundun.common.context.BaseContext;
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

import static com.createpen.fifteenhundun.common.constant.UserStatusConstant.*;

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
//    @Caching(
//            evict={
//                    @CacheEvict(cacheNames = "usersCache" ,allEntries = true),
//                    @CacheEvict(cacheNames = "userCache" ,key = "#username")
//            }
//    )
    @Override
    public Integer changeStatus(String username) {
        User operateUser = um.getUserByUserName( username);
        if(operateUser == null)
        {
            throw new UserNotFoundException("用户不存在");
        }
        Integer status = operateUser.getStatus();
        //检验权限是否允许这样做
        String currentUsername = BaseContext.getCurrentUsername();
        User currentUser= um.getUserByUserName(currentUsername);

        if (currentUser.getStatus()<= operateUser.getStatus() || currentUser.getStatus()<=(-operateUser.getStatus())){
            throw new InsufficientPermissionsException("权限不够的...(就算你是鹈鹕也不能吃同事/w\\)");
        }
        switch (status){
            case STATUS_USER:{
                operateUser.setStatus(STATUS_USER_BAN);
                break;
            }
            case STATUS_USER_BAN:{
                operateUser.setStatus(STATUS_USER);
                break;
            }
            case STATUS_ADMIN_BAN:{
                operateUser.setStatus(STATUS_ADMIN);
                break;
            }
            case STATUS_ADMIN:{
                operateUser.setStatus(STATUS_ADMIN_BAN);
                break;
            }
            case STATUS_WUQI:{
                throw new InsufficientPermissionsException("你干嘛 不许你这样对芜柒sama");
            }
            default:{
                log.warn("用户状态未定义 系统已经将状态设为默认态");
                operateUser.setStatus(STATUS_USER_BAN);
                break;
            }
        }

        um.updateUserByUserName(operateUser);
        log.info("用户状态已修改");
        log.info("用户状态:{}", operateUser.getStatus());
        return operateUser.getStatus();
    }

    @Override
//    @CacheEvict(cacheNames = "usersCache" ,allEntries = true)
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
//            us.cacheUser(u);
            return;
        }
        throw new UserExistedException("用户已存在");


    }

    @Override
//todo:为分页查询用户添加缓存
    public PageVO getUsers(GetUsersDTO dto) {
        log.info("开始分页查询用户");
        log.info("分页参数:{}",dto);
        PageHelper.startPage(dto.getPage(),dto.getSize());
        List<User> userList = am.getUsers(dto.getName(),dto.getStatus(),dto.getBeginTime(),dto.getEndTime(),dto.getLoginDays());
        Page<User> p = (Page<User>)userList;
        return new PageVO(p.getTotal(),p.getResult());
    }
    @Override
//todo:为分页查询日志添加缓存
    public PageVO getLogs(GetLogsDTO dto) {
        log.info("{}",dto);
        PageHelper.startPage(dto.getPage(),dto.getSize());
        List<OperateLog> l = am.getLogs(dto);
        Page p = (Page) l;

        return new PageVO(p.getTotal(),p.getResult());
    }

    @Override
    public String makeInviteCode() {
        User u =new User();
        u.setPasswordHash(PasswordUtil.addMi("fqeqvbqeiovfbqiudbv"));
        u.setStatus(STATUS_ADMIN);
        //todoed: 生成邀请码
        u.setInviteCode(RandomCodeUtil.makeComplexCode(10));
        u.setUsername("管理员待注册"+u.getId());
        um.insertUser(u);
        return u.getInviteCode();
    }
//    @Override
//    public void deleteUserByUserName(String username) {
//        um.deleteUserByUserName(username);

//    }


}
