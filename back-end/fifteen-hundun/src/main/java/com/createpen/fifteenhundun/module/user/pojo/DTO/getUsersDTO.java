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
    private LocalDateTime beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endTime;



}
