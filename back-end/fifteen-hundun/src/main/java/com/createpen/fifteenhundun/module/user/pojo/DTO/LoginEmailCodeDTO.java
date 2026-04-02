package com.createpen.fifteenhundun.module.user.pojo.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginEmailCodeDTO {
    @NotEmpty
    @Email
    private String email;
    //十位非空
    @NotEmpty
    @Size(min = 10, max = 10,message="验证码长度不对哦...")
    private String code;

}
