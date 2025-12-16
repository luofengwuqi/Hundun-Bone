package com.createpen.fifteenhundun.module.user.service;

import com.createpen.fifteenhundun.module.user.pojo.DTO.PageUsersVO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.addAdminDTO;

public interface AdminService {

    Integer getUserNumber();


    void changeStatus(String username);


    void addAdmin(addAdminDTO dto);

    PageUsersVO getUsers(Integer page, Integer size);
}
