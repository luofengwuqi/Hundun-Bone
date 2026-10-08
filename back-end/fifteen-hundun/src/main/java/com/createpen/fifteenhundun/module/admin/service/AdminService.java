package com.createpen.fifteenhundun.module.admin.service;

import com.createpen.fifteenhundun.module.user.pojo.DTO.GetLogsDTO;
import com.createpen.fifteenhundun.module.admin.pojo.DTO.AddAdminDTO;
import com.createpen.fifteenhundun.module.admin.pojo.DTO.GetUsersDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.UpdateUserDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.OperateLog;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.pojo.VO.PageVO;
import jakarta.validation.Valid;

public interface AdminService {



    Integer getUserNumber();


    Integer changeStatus(String username);


    void addAdmin(AddAdminDTO dto);

    PageVO<User> getUsers(GetUsersDTO dto);

    String makeInviteCode();

//    void deleteUserByUserName(String username);

    PageVO<OperateLog> getLogs(@Valid GetLogsDTO dto);

    void deleteUserById(Long id);

    void updateUser(UpdateUserDTO dto);

    User getUserInfoById(Long id);
}
