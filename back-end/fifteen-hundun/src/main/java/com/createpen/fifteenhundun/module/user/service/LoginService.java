package com.createpen.fifteenhundun.module.user.service;

import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginEmailCodeDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginPhonePasswordDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginUsernamePasswordRequestDTO;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;
import jakarta.validation.Valid;

public interface LoginService {
    LoginRequestVO loginPhonePassword(LoginPhonePasswordDTO dto);

    LoginRequestVO loginUsernamePassword(@Valid LoginUsernamePasswordRequestDTO l);

    LoginRequestVO loginEmailCode(LoginEmailCodeDTO dto);
}
