package com.createpen.fifteenhundun.module.user.controller;

import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.user.pojo.DTO.DeleteUsersDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.GetUsersDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.RegisterDTO;
import com.createpen.fifteenhundun.module.user.pojo.VO.PageVO;
import com.createpen.fifteenhundun.module.user.pojo.VO.RegisterVO;
import com.createpen.fifteenhundun.module.user.service.AdminService;
import com.createpen.fifteenhundun.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService us;
    @Autowired
    private AdminService as;
    @PostMapping
    public Result<RegisterVO> register(@Valid @RequestBody RegisterDTO dto)
    {
        log.info("用户注册");
        log.info("已经接收到请求:RegisterDTO信息为{}",dto.toString());
        RegisterVO r = us.register(dto);
        return Result.success(r);
    }

    //todoed:优化这里的删除逻辑 改为批量删除
    @PostMapping("/page")
    //@Cacheable(cacheNames = "usersCache" ,key = "#dto.page + '_' + #dto.size + '_' + #dto.name + '_' + #dto.status")
    public Result<PageVO> getUsers(@Valid @RequestBody GetUsersDTO dto)
    {
        log.info("{}",dto);
        //page:当前的页数
        //size:前端指定的每页的条数
        log.info("分页查询用户");
        PageVO vo = as.getUsers(dto);
        return Result.success(vo);
    }

    @DeleteMapping
//    @CacheEvict(cacheNames = "usersCache" ,allEntries = true)
    public Result<Void> deleteUsers(@Valid @RequestBody DeleteUsersDTO dto) throws Exception {
        log.info("用户删除");
//        log.info("即将进行用户删除 捕获到信息:{}",dto);
        us.deleteUsers(dto);
        return Result.success();
    }
}
