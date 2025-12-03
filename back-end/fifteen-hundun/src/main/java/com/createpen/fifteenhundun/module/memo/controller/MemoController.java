package com.createpen.fifteenhundun.module.memo.controller;

import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.memo.pojo.DTO.AddMemoDTO;
import com.createpen.fifteenhundun.module.memo.service.MemoService;
import com.createpen.fifteenhundun.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("memo")
@Slf4j
public class MemoController {

    @Autowired
    private MemoService ms;
    @Autowired
    private UserService us;

    @PostMapping("/add")
    public Result addMemo(@Valid @RequestBody AddMemoDTO dto)
    {
        log.info("添加备忘录");
         ms.addMemo(dto);
        return Result.success();
    }

}
