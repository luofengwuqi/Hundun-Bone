package com.createpen.fifteenhundun.module.memo.service;

import com.createpen.fifteenhundun.module.memo.pojo.DTO.AddMemoDTO;
import jakarta.validation.Valid;

public interface MemoService {

    void addMemo(@Valid AddMemoDTO dto);
}
