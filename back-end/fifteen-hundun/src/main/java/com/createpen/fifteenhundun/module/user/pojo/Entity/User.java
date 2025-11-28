package com.createpen.fifteenhundun.module.user.pojo.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
        /**
         * 用户ID（主键）
         */
        private Long id;

        /**
         * 用户名（唯一）
         */
        private String username;

        /**
         * 邮箱（唯一）
         */
        private String email;

        /**
         * 密码哈希值（存储加密后的密码，前端不可返回）
         */
        @JsonIgnore // 序列化时忽略，避免返回给前端
        private String passwordHash;

        /**
         * 昵称
         */
        private String nickname;

        /**
         * 头像URL
         */
        private String avatarUrl;

        /**
         * 手机号
         */
        private String phone;

        /**
         * 用户状态：1-正常，0-禁用
         */
        private Integer status;

        /**
         * 创建时间
         */
        private LocalDateTime createdAt;

        /**
         * 更新时间
         */
        private LocalDateTime updatedAt;

        /**
         * 最后登录时间
         */
        private LocalDateTime lastLoginAt;

        /**
         * 用户个性化配置（一对一关联 user_settings 表）
         * 可根据业务需求拆分为独立的 UserSettings 类
         */
        private UserSettings userSettings;

        /**
         * 用户个性化配置实体类
         * 对应数据库 user_settings 表
         */
}
