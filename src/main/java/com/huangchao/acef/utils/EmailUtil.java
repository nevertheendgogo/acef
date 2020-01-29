package com.huangchao.acef.utils;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 本类为邮箱发送工具类
 */
public class EmailUtil {

    private String fromEmail;// 发件人电子邮箱
    private String email;// 收件人邮箱
    private String authorizationCode;// 激活码

    private final static Logger logger = (Logger) LoggerFactory.getLogger(EmailUtil.class);

    public EmailUtil(String fromEmail, String email, String authorizationCode) {
        this.fromEmail = fromEmail;
        this.email = email;
        this.authorizationCode = authorizationCode;
    }

    public int run(String topic, String content) {
        // 1.创建连接对象javax.mail.Session
        // 2.创建邮件对象 javax.mail.Message
        // 3.发送一封激活邮件
        String host = "smtp.163.com"; // 指定发送邮件的主机smtp.qq.com(QQ)|smtp.163.com(网易)

        Properties properties = System.getProperties();// 获取系统属性

        properties.setProperty("mail.smtp.host", host);// 设置邮件服务器
        properties.setProperty("mail.smtp.auth", "true");// 打开认证
        try {
            //QQ邮箱需要下面这段代码，163邮箱不需要
//            MailSSLSocketFactory sf = new MailSSLSocketFactory();
//            sf.setTrustAllHosts(true);
//            properties.put("mail.smtp.ssl.enable", "true");
//            properties.put("mail.smtp.ssl.socketFactory", sf);


            // 1.获取默认session对象
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, authorizationCode); // 发件人邮箱账号、授权码
                }
            });

            // 2.创建邮件对象
            Message message = new MimeMessage(session);
            // 2.1设置发件人
            message.setFrom(new InternetAddress(fromEmail));
            // 2.2设置接收人
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            // 2.3设置邮件主题
            message.setSubject(topic);

            message.setContent(content, "text/html;charset=UTF-8");
            logger.info("发送邮件，收件邮箱为：{}",email);
            // 3.发送邮件
            Transport.send(message);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
