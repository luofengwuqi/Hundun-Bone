package com.createpen.fifteenhundun.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 阿里云OSS工具类
 * AI生成
 */
@Slf4j
@Component
public class AliOSSUtil {

@Value("${aliyun.endpoint}")
private String endpoint;

@Value("${aliyun.access-key-id}")
private String accessKeyId;

@Value("${aliyun.access-key-secret}")
private String accessKeySecret;

@Value("${aliyun.bucket-name}")
private String bucketName;

@Value("${aliyun.domain}")
private String domain;

    /**
     * 上传文件到OSS
     * @param file 前端上传的文件
     * @param folder OSS存储文件夹（如memos/avatars/）
     * @return 文件访问URL
     */
    public String upload(MultipartFile file, String folder) throws IOException {
        // 1. 生成唯一文件名（避免重复）
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix; // 如：a1b2c3d4-xxxx-xxxx-xxxx-xxxxxxx.png
        String objectName = folder + fileName; // 完整OSS路径：memos/avatars/a1b2c3d4.png

        // 2. 获取文件输入流
        InputStream inputStream = file.getInputStream();

        // 3. 创建OSS客户端并上传文件
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            // 上传文件
            ossClient.putObject(new PutObjectRequest(bucketName, objectName, inputStream));
            log.info("文件上传OSS成功：{}", objectName);
            // 4. 返回文件访问URL
            return domain + "/" + objectName; // 如：https://xxx.oss-cn-hangzhou.aliyuncs.com/memos/avatars/a1b2c3d4.png
        } catch (Exception e) {
            log.error("文件上传OSS失败", e);
            throw new RuntimeException("文件上传失败");
        } finally {
            // 关闭OSS客户端和输入流
            if (ossClient != null) {
                ossClient.shutdown();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * 删除OSS文件
     * @param fileUrl 文件访问URL
     */
    public void delete(String fileUrl) {
        // 解析URL中的objectName（去掉域名部分）
        String objectName = fileUrl.replace(domain + "/", "");
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ossClient.deleteObject(bucketName, objectName);
            log.info("文件删除OSS成功：{}", objectName);
        } catch (Exception e) {
            log.error("文件删除OSS失败", e);
            throw new RuntimeException("文件删除失败");
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}