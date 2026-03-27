package com.createpen.fifteenhundun.module.user.service;

import com.createpen.fifteenhundun.module.user.pojo.DTO.DeleteUsersDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginUsernamePasswordRequestDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.RegisterDTO;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;
import com.createpen.fifteenhundun.module.user.pojo.VO.RegisterVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

    LoginRequestVO loginUsernamePassword(@Valid LoginUsernamePasswordRequestDTO l);


    @Transactional
    RegisterVO register(RegisterDTO dto);

    void deleteUsers(@Valid DeleteUsersDTO dto) throws  Exception;


    //根据username得到id
//    Long getIdByUsername(String username);

}
