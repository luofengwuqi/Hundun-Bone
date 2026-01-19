package com.createpen.fifteenhundun.module.user.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class PageVO implements Serializable {
    private Long total;
//    private Integer pageNum;
//    private Integer pageSize;
    private List list;

}
