package com.createpen.fifteenhundun.common.util;

import lombok.extern.slf4j.Slf4j;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

import static com.createpen.fifteenhundun.common.constant.RedisConstant.EMAIL_CODE_EXPIRE_TIME;

@Slf4j
public class EmailCodeUtil {
    // 邮箱配置（修改为你的 QQ 邮箱 + 授权码）
    private static final String EMAIL_FROM = "luofengwuqi@qq.com";
    private static final String EMAIL_AUTH_CODE = "wfmcxnshammdjici";
    private static final String SMTP_HOST = "smtp.qq.com";

    /**
     * 生成 6 位随机数字验证码
     */
    public static String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    /**
     * 发送验证码到指定邮箱
     * @param toEmail 接收方邮箱
     * @param code 验证码
     * @return 发送结果
     */
    public static boolean sendEmailCode(String toEmail, String code) {
        // 1. 配置 SMTP 服务器参数
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", SMTP_HOST);
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");

        // 2. 创建邮箱会话（登录邮箱）
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_AUTH_CODE);
            }
        });

        try {
            // 3. 构建邮件内容
            String content = "Ciallo～(∠・ω< )⌒★\n\n您期待已久的验证码是------\n\n" + code + "\n\n只在"+EMAIL_CODE_EXPIRE_TIME+" 分钟内有效!\n\n祝您今天也有好心情!";
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM)); // 发件人
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail)); // 收件人
            message.setSubject("【身份验证】验证码!"); // 邮件标题
            message.setText(content); // 邮件内容

            // 4. 发送邮件
            Transport.send(message);
            System.out.println("验证码发送成功！验证码：" + code);
            return true;
        } catch (MessagingException e) {
            log.error("发送邮件失败，邮箱：{}", toEmail, e);
            return false;
        }
    }

}