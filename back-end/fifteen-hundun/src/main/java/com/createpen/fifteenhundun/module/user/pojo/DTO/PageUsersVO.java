package com.createpen.fifteenhundun.module.user.pojo.DTO;

import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.fasterxml.classmate.TypeResolver;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Data
@AllArgsConstructor
public class PageUsersVO {
    private Long total;
//    private Integer pageNum;
//    private Integer pageSize;
    private List list;

}
