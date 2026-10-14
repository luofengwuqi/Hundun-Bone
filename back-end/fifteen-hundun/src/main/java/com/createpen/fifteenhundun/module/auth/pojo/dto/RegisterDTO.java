package com.createpen.fifteenhundun.module.auth.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 10, message = "用户名长度在1-10之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度在6-20之间")
    private String password;

//    @NotBlank(message = "测试密码不能为空")
//    @Size(min = 6, max = 20, message = "密码长度在6-20之间")
//    private String checkPassword;

    private String avatarUrl;

  // 手机号（可选）
@Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
private String phone;

// 邮箱（可选，唯一）
@Email(message = "邮箱格式错误")
private String email;


    //重写toString方法
    @Override
    public String toString() {
        return "RegisterDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
