package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 邮件发送功能。将发送邮件的功能委托sina邮箱去做。
 *
 * 整合日志。
 *
 */
@Component
public class MailClient {

    private static final Logger logger= LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    //发件人
    @Value("${spring.mail.username}")
    private String from;

    /**
     * MimeMessageHelper帮助构建MimeMessage的内容。
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    public void sendMail(String to,String subject,String content){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message);
            //发件人、收件人、主题、内容
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content,true);
            //发送邮件
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("邮件发送失败"+e.getMessage());
        }
    }



}
