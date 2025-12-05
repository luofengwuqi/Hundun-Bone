package com.createpen.fifteenhundun.module.user.controller;

import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.user.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping
public class CommonController {

    @Autowired
    private com.createpen.fifteenhundun.common.utils.AliOSSUtil aliOssUtil;

    @Autowired
    private CommonService cs;
    /**
     * 上传图片/附件
     */
    @PostMapping("/file/add")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl =  cs.addFile(file);
        if(fileUrl == null)
        {
            return Result.fail(500, "文件上传失败");
        }
        return Result.success("文件上传成功", fileUrl);

    }
}