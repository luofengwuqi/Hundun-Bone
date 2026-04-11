package com.createpen.fifteenhundun.module.user.service.serviceimpl;

//import com.createpen.fifteenhundun.common.constant.ImportantConstant;
import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.common.exception.MyException.InsufficientPermissionsException;
import com.createpen.fifteenhundun.common.exception.MyException.LoginException;
import com.createpen.fifteenhundun.common.exception.MyException.UserExistedException;
import com.createpen.fifteenhundun.common.exception.MyException.UserNotFoundException;
import com.createpen.fifteenhundun.common.util.PasswordUtil;
import com.createpen.fifteenhundun.common.util.RandomCodeUtil;
import com.createpen.fifteenhundun.common.util.Redis.UserRedisUtil;
import com.createpen.fifteenhundun.module.user.mapper.AdminMapper;
import com.createpen.fifteenhundun.module.user.mapper.UserMapper;
import com.createpen.fifteenhundun.module.user.pojo.DTO.GetLogsDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.OperateLog;
import com.createpen.fifteenhundun.module.user.pojo.VO.PageVO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.AddAdminDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.GetUsersDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.service.AdminService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.createpen.fifteenhundun.common.constant.RedisConstant.*;
import static com.createpen.fifteenhundun.common.constant.Status.UserStatusConstant.*;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper am;
    @Autowired
    private UserMapper um;

    @Autowired
    private UserServiceImpl us;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRedisUtil userRedisUtil;


    @Override
    public Integer getUserNumber() {
        return am.getUserNumber();
    }



    @Transactional
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
            case STATUS_SUPER_ADMIN:{
                throw new InsufficientPermissionsException("你干嘛 不许你这样对芜柒 sama");
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
        
        //清除用户查询缓存
        userRedisUtil.clearUsersCache();
        
        return operateUser.getStatus();
    }

    @Override
    @Transactional
    public void addAdmin(AddAdminDTO dto) {
        log.info("开始添加管理员");
        //这里的逻辑应该只有当时写代码的我还有上帝才能看得懂 这里就当是一个教训
        try{
            us.getValidUserByUserName(dto.getUsername());
        }catch (UserNotFoundException e)//不存在才继续 真拧巴
        {
            String importantPassword = am.selectInvitePassword(dto.getImportantPassword());
            log.info("检索到的邀请码#{}",importantPassword);
            if(importantPassword==null){
                throw new LoginException("你貌似没有被邀请 你干嘛啊??");
            }
            log.info("重要密码已校验通过");

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
            
            //清除用户查询缓存
            userRedisUtil.clearUsersCache();
            
            return;
        }
        throw new UserExistedException("用户已存在");


    }

    @Override
    public PageVO<User> getUsers(GetUsersDTO dto) {
        log.info("开始分页查询用户");
        log.info("分页参数:{}",dto);
        
        //生成缓存 key
        String cacheKey = generateUsersCacheKey(dto);
        log.info("缓存 key:{}", cacheKey);
        
        //1. 先尝试从 Redis 获取缓存
        Object cachedData = redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) {
            log.info("命中缓存，从 Redis 获取数据");
            try {
                String json = (String) cachedData;
                if (json.isEmpty()) {
                    return new PageVO<>(0L, null);
                }
                return objectMapper.readValue(json, 
                    objectMapper.getTypeFactory().constructParametricType(PageVO.class, User.class));
            } catch (JsonProcessingException e) {
                log.error("反序列化缓存数据失败", e);
            }
        }
        
        log.info("缓存未命中，查询数据库");
        
        //2. 缓存未命中，查询数据库
        PageHelper.startPage(dto.getPage(),dto.getSize());
        List<User> userList = am.getUsers(dto.getName(),dto.getStatus(),dto.getBeginTime(),dto.getEndTime(),dto.getLoginDays());
        if (userList.isEmpty()){
            //解决穿透问题 如果是空 那么直接在redis中设置一个空值
            redisTemplate.opsForValue().set(cacheKey, "", NULL_EXPIRE_TIME, TimeUnit.MINUTES);
            throw new UserNotFoundException("没有查到结果哦");
        }
        Page<User> p = (Page<User>)userList;
        PageVO<User> pageVO = new PageVO<>(p.getTotal(),p.getResult());

        //3. 将结果存入 Redis 缓存
        try {
            String json = objectMapper.writeValueAsString(pageVO);
            redisTemplate.opsForValue().set(cacheKey, json, USERS_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
            log.info("已将查询结果存入 Redis 缓存，过期时间：{}分钟", USERS_CACHE_EXPIRE_TIME);
        } catch (JsonProcessingException e) {
            log.error("序列化缓存数据失败", e);
        }
        
        return pageVO;
    }
    
    /**
     * 生成用户查询缓存 key
     * 根据查询条件生成唯一的 key
     */
    private String generateUsersCacheKey(GetUsersDTO dto) {
        StringBuilder keyBuilder = new StringBuilder(USERS_CACHE_PATH);
        keyBuilder.append("page_").append(dto.getPage()).append("_");
        keyBuilder.append("size_").append(dto.getSize()).append("_");
        
        if (dto.getName() != null && !dto.getName().isEmpty()) {
            keyBuilder.append("name_").append(dto.getName()).append("_");
        }
        
        if (dto.getStatus() != null) {
            keyBuilder.append("status_").append(dto.getStatus()).append("_");
        }
        
        if (dto.getBeginTime() != null) {
            keyBuilder.append("begin_").append(dto.getBeginTime().toString()).append("_");
        }
        
        if (dto.getEndTime() != null) {
            keyBuilder.append("end_").append(dto.getEndTime().toString()).append("_");
        }
        
        if (dto.getLoginDays() != null) {
            keyBuilder.append("logindays_").append(dto.getLoginDays()).append("_");
        }
        
        return keyBuilder.toString();
    }

    @Override
//todo:为分页查询日志添加缓存
    public PageVO<OperateLog> getLogs(GetLogsDTO dto) {
        log.info("{}",dto);
        PageHelper.startPage(dto.getPage(),dto.getSize());
        List<OperateLog> l = am.getLogs(dto);
        Page<OperateLog> p = (Page<OperateLog>) l;

        return new PageVO<>(p.getTotal(),p.getResult());
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("{}",id);
        User u =  us.selectUserById(id);
        if(u == null){
            throw new UserNotFoundException("用户不存在");
        }
        if(u.getStatus() == STATUS_SUPER_ADMIN){
            throw new InsufficientPermissionsException("不许你这样对管理员 sama");
        }
        um.deleteUserById(id);
        
        //清除用户查询缓存
        userRedisUtil.clearUsersCache();
    }
    
    /**
     * 清除所有用户查询缓存
     * 使用 pattern 删除匹配的所有 key
     */


    @Override
    public String makeInviteCode() {
        User u =new User();
        u.setPasswordHash(PasswordUtil.addMi("fqeqvbqeiovfbqiudbv"));
        u.setStatus(STATUS_ADMIN_BAN);
        //todoed: 生成邀请码
        String code = RandomCodeUtil.makeComplexCode(10);
        u.setInviteCode(code);
        u.setUsername("管理员待注册"+code.substring(0,2));
        um.insertUser(u);
        return u.getInviteCode();
    }
//    @Override
//    public void deleteUserByUserName(String username) {
//        um.deleteUserByUserName(username);

//    }


}
