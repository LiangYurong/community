package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger= LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;


    /**
     * 跳转到登录页面
     * @return
     */
    @GetMapping("/login")
    public String getLoginPage(){
        return "site/login";
    }

    /**
     * 跳转到注册页面
     * @return
     */
    @GetMapping("/register")
    public String getRegisterPage(){
        return "site/register";
    }


    /**
     * 只要前端页面传过来的值对应User的属性，SpringMVC就能够自动匹配。
     * @param model
     * @param user
     * @return
     */
    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        //如果用户注册成功，发送一封激活邮件
        if(map==null || map.isEmpty()){
            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("target","/index"); //激活成功后，跳转到首页
            return "site/operate-result";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "site/register";
        }
    }


    /**
     * 验证邮件，是否激活了用户。
     * http://localhost:8081/community/activation/101/code
     * 1、激活成功后，跳转到登录页面
     * 2、已经激活过的账号，再次激活显示无效，直接跳转到首页
     * 3、激活失败，直接跳转到登录界面
     * @param model
     * @param userId
     * @param code
     * @return
     */
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model,@PathVariable("userId") int userId,@PathVariable("code") String code){
        int result = userService.activation(userId, code);
        if(result==ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您的账号已经可以正常使用");
            model.addAttribute("target","/login");
        }else if(result==ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，该账号已经激活过了");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败，您提供的激活码不正确");
            model.addAttribute("target","/index");
        }
        return "site/operate-result";
    }


    /**
     * 获取生成验证码图片的路径
     *
     * 生成验证码之后，服务端需要将这个验证码记住。因为验证码是敏感数据，所以不能存于浏览器的cookie，而是将其存储在服务端的session。
     * @param response
     */
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        //生成验证码的字符串
        String text=kaptchaProducer.createText();
        //生成字符串对应的图片
        BufferedImage image = kaptchaProducer.createImage(text);
        //将验证码存入session
        session.setAttribute("text",text);
        //将图片输出给浏览器
        response.setContentType("/image/png");
        try {
            //不用手动关闭这些流，由SpringMVC自动维护。
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败"+e.getMessage());
        }
    }
}
