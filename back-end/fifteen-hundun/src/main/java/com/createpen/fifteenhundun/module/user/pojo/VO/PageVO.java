package com.createpen.fifteenhundun.module.user.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageVO {
    private Long total;
//    private Integer pageNum;
//    private Integer pageSize;
    private List list;

}
