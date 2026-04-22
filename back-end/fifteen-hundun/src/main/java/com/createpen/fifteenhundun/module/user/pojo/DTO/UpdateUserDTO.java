package com.createpen.fifteenhundun.module.user.pojo.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateUserDTO {
    @NotNull(message = "用户旧id不能为空")
    private Long oldId;

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String phone;

    private String email;

    private LocalDateTime updatedAt;
}
