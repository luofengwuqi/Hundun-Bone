package com.createpen.fifteenhundun.module.user.service.serviceimpl;

import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.user.mapper.CommonMapper;
import com.createpen.fifteenhundun.module.user.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private CommonMapper cm;

    @Autowired
    private com.createpen.fifteenhundun.common.util.AliOSSUtil aliOssUtil;

    @Override
    public String addFile(MultipartFile file) {
        try {
            // 上传到OSS的 {用户名}/files/文件夹下
            //todoed: 获取当前用户名字
            String username = BaseContext.getCurrentUsername();
            log.info("当前用户名：{}", username);
            String savePath = username+"/files/";
            String fileUrl = aliOssUtil.upload(file, savePath);
            return fileUrl;

        } catch (Exception e) {
            return null;
        }

    }
}
