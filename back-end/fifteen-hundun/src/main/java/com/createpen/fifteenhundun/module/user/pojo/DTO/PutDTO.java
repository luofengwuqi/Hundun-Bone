package com.createpen.fifteenhundun.module.user.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PutDTO {
    @NotBlank
    private String huh;
    @NotBlank
    private String password;
}
