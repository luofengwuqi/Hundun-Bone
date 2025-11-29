package com.createpen.fifteenhundun.module.user.service;

import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginRequestDTO;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;
import jakarta.validation.Valid;

public interface UserService {

    LoginRequestVO login(@Valid LoginRequestDTO l);
}
