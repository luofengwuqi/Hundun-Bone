package com.createpen.fifteenhundun.module.user.service;

import com.createpen.fifteenhundun.module.user.pojo.DTO.GetLogsDTO;
import com.createpen.fifteenhundun.module.user.pojo.VO.PageVO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.AddAdminDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.GetUsersDTO;
import jakarta.validation.Valid;

public interface AdminService {

    Integer getUserNumber();


    void changeStatus(String username);


    void addAdmin(AddAdminDTO dto);

    PageVO getUsers(GetUsersDTO dto);

    String makeInviteCode();

    void deleteUserByUserName(String username);

    PageVO getLogs(@Valid GetLogsDTO dto);
}
