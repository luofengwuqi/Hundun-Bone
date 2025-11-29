package com.createpen.fifteenhundun.module.user.pojo.VO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestVO {
    @NotBlank(message = "我也不知道为啥但是token没有值")
    private String token;

    private String username;
}
