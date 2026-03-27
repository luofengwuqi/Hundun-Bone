package com.createpen.fifteenhundun.module.user.service;

import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginPhonePasswordDTO;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;

public interface LoginService {
    LoginRequestVO loginPhonePassword(LoginPhonePasswordDTO dto);
}
