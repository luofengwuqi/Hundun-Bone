package com.createpen.fifteenhundun.module.user.service.serviceimpl;

import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.common.exception.MyException.*;
import com.createpen.fifteenhundun.common.util.AliOSSUtil;
import com.createpen.fifteenhundun.common.util.PasswordUtil;
import com.createpen.fifteenhundun.module.user.cache.UserRedisService;
import com.createpen.fifteenhundun.module.user.mapper.UserMapper;
import com.createpen.fifteenhundun.module.admin.pojo.DTO.DeleteUsersDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.PutDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.RegisterDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.UpdatePasswordDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.pojo.VO.RegisterVO;
import com.createpen.fifteenhundun.module.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.createpen.fifteenhundun.common.constant.redis.RedisConstant.EMAIL_CODE_PATH;
import static com.createpen.fifteenhundun.common.constant.oss.UserOssContant.USER_AVATAR_FOLDER;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    //26/3/26 芜柒:看见之前的代码想把自己打一顿怎么办
    //26/4/1  芜柒:腿打断
    @Autowired
    private UserMapper um;
    @Autowired
    private UserRedisService userRedisService;
    @Autowired
    private AliOSSUtil aliOssUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Transactional(rollbackFor = Exception.class)
    public RegisterVO register(RegisterDTO dto) {
        //1检验用户是否已经存在
        checkUserNotExists(dto.getUsername(),dto.getPhone(),dto.getEmail());
        //2 不存在 插入
        User u = new User();
        BeanUtils.copyProperties(dto,u);
        u.setPasswordHash(PasswordUtil.addMi(dto.getPassword()));
        um.insertUser(u);
        //3 删除管理端用户查询的分页缓存
        userRedisService.clearUsersCache();
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
            List<User> validUserListByUserNameList = getValidUserListByUserNameList(dto.getUsernames());
            if (validUserListByUserNameList.isEmpty()) {
                return;
            }
            //2 删除这些合法参数
            um.deleteUserByUserNameList(dto.getUsernames());
        } else {
            throw new IllegalAccessException("用户已被禁用!");
        }
    }

    @Override
    public void updateAvatar(MultipartFile file) {
        //构建文件路径
        Long currentId = BaseContext.getCurrentId();
        String currentUsername = BaseContext.getCurrentUsername();
        String fileName = aliOssUtil.makeUUIDFileName(file);
        String objectName = currentUsername + USER_AVATAR_FOLDER  + fileName;
        //拿到用户对象
        User u;
        User userInRedis = userRedisService.getUserInRedis(currentId);
        if(userInRedis!=null){
            u = userInRedis;
        }else{
            u = um.selectUserById(currentId);
            userRedisService.saveUserInRedis(u);
        }
        if(u==null){
            throw new UserNotFoundException("用户不存在");
        }
        //检查用户是否已有头像 如果有 删除
        if(u.getAvatarUrl() != null){
            aliOssUtil.delete(u.getAvatarUrl());
        }
        //在oss中添加
        try {
            aliOssUtil.upload(file, objectName);
        } catch (Exception e) {
            throw new OSSException("上传头像失败了...是oss的问题...");
        }
        //在数据库中改写头像字段

        try {
            um.updateUserAvatarPathById(currentId, objectName);
        }catch (Exception e){
            aliOssUtil.delete(objectName);
            throw new SQLException("更新输入库失败...");
        }
        //删除用户缓存
        userRedisService.clearUserdata(BaseContext.getCurrentId());

    }

    //辅助性方法 注册时候验证username  email phone 的唯一性
    public void checkUserNotExists(String username,String phone,String email)
    {
        Integer id = um.checkUserNotExists(username,phone,email);
        //无事发生
        if(id == null || id == 0) {
            return;
        }
        //出逝了
        List<String> occupiedFields = new ArrayList<>();
        if(phone != null) occupiedFields.add("手机号");
        if(email != null) occupiedFields.add("邮箱");
        
        throw new UserExistedException("提交的用户名/" + String.join("/", occupiedFields) + "已被占用...");
    }


    //辅助性方法 用户是否存在
    //1 单个用户
    public User getValidUserById(Long id){
        User u = userRedisService.getUserInRedis(id);
        if (u != null){
            return u;
        }
        //缓存未命中
        u =  um.selectUserById(id);
        if(u==null){
            throw new UserNotFoundException("用户不存在");
        }
        userRedisService.saveUserInRedis(u);
        return u;
    }
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
        // 先从redis里面那
        User userInRedis = userRedisService.getUserInRedis(id);
        if( userInRedis != null ){
            return userInRedis;
        }
        User u = um.selectUserById(id);
        userRedisService.saveUserInRedis(u);
        return u;
    }

    @Override
    public void updateNickname(PutDTO dto) {
        //获取当前用户
        Long id = BaseContext.getCurrentId();
        User user = getValidUserById(id);
        //校验密码
        if(PasswordUtil.check(dto.getPassword(),user.getPasswordHash())){
            throw new PasswordException("密码输错了哦...");
        }
        //更新用户
        String nickname = dto.getHuh();
        user.setNickname(nickname);
        um.updateNickNameById(nickname,id);
        userRedisService.saveUserInRedis(user);
    }

    @Override
    public void updateEmail(PutDTO dto) {
        //获取当前用户
        Long id = BaseContext.getCurrentId();
        User user = getValidUserById(id);
        //校验密码
        if(!PasswordUtil.check(dto.getPassword(),user.getPasswordHash())){
            throw new PasswordException("密码输错了哦...");
        }
        //更新用户
        String email = dto.getHuh();
        user.setEmail(email);
        try{
            um.updateEmailById(email,id);
        }catch (Exception e){
            throw new SQLException("这个邮箱已被使用...");
        }
        userRedisService.saveUserInRedis(user);
    }

    @Override
    public void updatePhone(PutDTO dto) {
        //获取当前用户
        Long id = BaseContext.getCurrentId();
        User user = getValidUserById(id);
        //校验密码
        if(!PasswordUtil.check(dto.getPassword(),user.getPasswordHash())){
            throw new PasswordException("密码输错了哦...");
        }
        //更新用户
        String phone = dto.getHuh();
        user.setPhone(phone);
        try{
            um.updatePhoneById(phone,id);
        }catch (Exception e){
            throw new SQLException("这个手机号已被使用...");
        }
        userRedisService.saveUserInRedis(user);
    }

    @Override
    public void updatePassword(UpdatePasswordDTO dto) {
        //1 在redis中读验证码
        User user = userRedisService.getUserInRedis(BaseContext.getCurrentId());
        if(user == null){
            user = getValidUserById(BaseContext.getCurrentId());
        }
        String key = EMAIL_CODE_PATH + user.getEmail();
        log.info("{}",key);
        String codeInRedis = stringRedisTemplate.opsForValue().get(key);
        log.info("{}",codeInRedis);
        //2 验证
        if (codeInRedis == null){
            throw new UserNotFoundException("验证码已过期");
        }
        if(!codeInRedis.equals(dto.getCode())){
            throw new PasswordException("验证码错误");
        }
        //3 修改
        um.updatePasswordById(PasswordUtil.addMi(dto.getPassword()),BaseContext.getCurrentId());
        //4 删除缓存
        userRedisService.clearUserdata(BaseContext.getCurrentId());

    }

}


