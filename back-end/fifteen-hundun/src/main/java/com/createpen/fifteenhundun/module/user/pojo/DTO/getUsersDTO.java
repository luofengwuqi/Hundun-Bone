package com.createpen.fifteenhundun.module.user.pojo.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class getUsersDTO {

    private Integer page;

    private Integer size;

    private String name;

    private Integer status;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    //设置默认值为很久以前
    private LocalDateTime beginTime = LocalDateTime.of(1900,1,1,0,0,0);

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    //设置默认值为现在
    private LocalDateTime endTime = LocalDateTime.now();

    //最近一次登录时间距今天数
    private Integer loginDays;



}
