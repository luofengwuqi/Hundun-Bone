package com.createpen.fifteenhundun.module.user.pojo.DTO;

import lombok.Data;

import java.util.List;

@Data
public class DeleteUsersDTO {
    private List<String> usernames;
}
