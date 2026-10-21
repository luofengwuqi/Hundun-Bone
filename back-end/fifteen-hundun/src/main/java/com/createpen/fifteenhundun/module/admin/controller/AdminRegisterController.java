package com.createpen.fifteenhundun.module.admin.controller;

import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.admin.pojo.DTO.AddAdminDTO;
import com.createpen.fifteenhundun.module.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员自助注册（凭邀请码，无需登录）
 * 与已登录管理员创建账号的 POST /admin 分离，避免 /admin 前缀被整体放行
 */
@Slf4j
@RestController
public class AdminRegisterController {

    private final AdminService adminService;

    public AdminRegisterController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/user/register/admin")
    public Result<Void> registerByInviteCode(@Valid @RequestBody AddAdminDTO dto) {
        log.info("管理员邀请码注册");
        adminService.addAdmin(dto);
        return Result.success();
    }
}
