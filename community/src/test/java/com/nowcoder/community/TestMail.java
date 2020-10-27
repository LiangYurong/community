package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = RunMyApplication.class)
public class TestMail {

    @Autowired
    private MailClient mailClient;

    /**
     * thymeleaf核心类
     */
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testMail(){
        mailClient.sendMail("794842744@qq.com","面试通知","请于明天上午十点来腾讯面试");
    }

    /**
     * 尝试发送html邮件
     */
    @Test
    public void testHtmlMail(){
        Context context=new Context();
        context.setVariable("username","sunday");
        //调用模板引擎生成动态html
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("794842744@qq.com","mailHtml",content);
    }
}
