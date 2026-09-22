package com.createpen.fifteenhundun.module.admin.pojo.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DeleteUsersDTO {
    @NotNull
    private List<String> usernames;

    private String password;
}
