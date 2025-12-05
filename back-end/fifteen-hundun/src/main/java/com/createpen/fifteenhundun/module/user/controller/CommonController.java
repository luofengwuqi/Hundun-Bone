package com.createpen.fifteenhundun.module.user.controller;

import com.createpen.fifteenhundun.common.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
public class CommonController {

    @Autowired
    private com.createpen.fifteenhundun.common.utils.AliOSSUtil aliOssUtil;

    /**
     * 上传图片/附件
     */
    @PostMapping("/file/add")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 上传到OSS的 {用户名}/files/文件夹下
            //todo: 获取当前用户名
//            String username = ;
            String savePath = null;
            String fileUrl = aliOssUtil.upload(file, savePath);
            return Result.success("文件上传成功", fileUrl);
        } catch (Exception e) {
            return Result.fail(500, "文件上传失败：" + e.getMessage());
        }
    }
}