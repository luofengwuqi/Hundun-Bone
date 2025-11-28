package com.createpen.fifteenhundun.common.util;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

//todo2修好了 我他妈也不知道咋回事 反正就是用不了这个了
@Component
public class PasswordUtil {

    // 加密强度（4-31，越高越安全）
    private static final int LOG_ROUNDS = 12;

    /**
     * 加密密码
     */
    public String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    /**
     * 校验密码（明文与密文匹配）
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
