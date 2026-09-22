package com.createpen.fifteenhundun.module.admin.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUsersDTO {

    private Integer page;

    private Integer size;

    private String name;

    private Integer status;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endTime;

    //最近一次登录时间距今天数
    private Integer loginDays;



}
