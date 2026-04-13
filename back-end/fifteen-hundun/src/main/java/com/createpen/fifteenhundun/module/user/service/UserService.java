package com.createpen.fifteenhundun.module.user.service;

import com.createpen.fifteenhundun.module.user.pojo.DTO.DeleteUsersDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.RegisterDTO;
import com.createpen.fifteenhundun.module.user.pojo.VO.RegisterVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {



    @Transactional
    RegisterVO register(RegisterDTO dto);

    void deleteUsers(@Valid DeleteUsersDTO dto) throws  Exception;

    void updateAvatar(MultipartFile file);


    //根据username得到id
//    Long getIdByUsername(String username);

}
