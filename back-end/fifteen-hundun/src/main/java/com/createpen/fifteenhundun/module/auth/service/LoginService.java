package com.createpen.fifteenhundun.module.auth.service;

import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginEmailCodeDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginEmailPassWordDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginPhonePasswordDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginUsernamePasswordRequestDTO;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;
import jakarta.validation.Valid;

public interface LoginService {
    LoginRequestVO loginPhonePassword(LoginPhonePasswordDTO dto);

    LoginRequestVO loginUsernamePassword(@Valid LoginUsernamePasswordRequestDTO l);

    LoginRequestVO loginEmailCode(LoginEmailCodeDTO dto);

    void sendEmailCode(String email);

    LoginRequestVO loginEmailPassword(LoginEmailPassWordDTO dto);

    void logout();
}
