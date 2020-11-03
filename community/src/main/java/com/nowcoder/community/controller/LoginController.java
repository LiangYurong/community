package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 *
 * 登录/验证/注册
 *
 * session是什么时候创建的？当浏览器A向服务器发送会话请求，并且访问服务器端某个能和浏览器开启会话的servlet程序时，
 *      服务器为这次会话创建一个HttpSession对象，并会这次会话分配一个唯一标识号id。
 *      并且这个HttpSession对象和这个id唯一对应。 
 */
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger= LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    /**
     * 跳转到登录页面
     * @return
     */
    @GetMapping("/login")
    public String getLoginPage(){
        return "site/login";
    }

    /**
     * 接收前端form表单传递过来的username,password,code，判断是否能够登录。
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param rememberme 是否选择记住
     * @param model model可以向前端返回数据
     * @param session 需要获取到验证码
     * @param response 需要建立一个cookie，服务端在cookie放ticket在里面，然后传回给客户端
     * @return
     */
    @PostMapping("/login")
    public String login(String username,String password,String code,boolean rememberme,
                        Model model,HttpSession session,HttpServletResponse response){
        String kaptcha =(String) session.getAttribute("kaptcha");
        //检查验证码
        if(!kaptcha.equalsIgnoreCase(code)|| StringUtils.isBlank(kaptcha)||StringUtils.isBlank(code)){
            model.addAttribute("codeMsg","验证码错误");
            return "site/login";
        }
        //检查账号密码
        int expiredSeconds= rememberme ? CommunityConstant.REMEMBER_EXPIRED_SECONDS : CommunityConstant.DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")){
            //如果map含有ticket，说明登录成功。此时还需要将ticket包在cookie里面发送给客户端。该cookie的访问路经应该是整个项目都可访问。
            Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "site/login";
        }
    }

    /**
     * 退出登录。需要获取到cookie里面的ticket，进而修改登录的状态status=1（登录失效状态）.
     *
     * 重定向默认是get请求。
     * @return
     */
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
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
     * 获取生成验证码图片的路径。主要是login.html的刷新验证码按钮，在前端点击刷新就会调用这个方法。
     *
     * 生成验证码之后，服务端需要将这个验证码记住。因为验证码是敏感数据，所以不能存于浏览器的cookie，而是将其存储在服务端的session。这个验证码用于登录时候。
     * @param response
     */
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        //生成验证码的字符串
        String text=kaptchaProducer.createText();
        //生成字符串对应的图片
        BufferedImage image = kaptchaProducer.createImage(text);
        //将验证码存入session
        session.setAttribute("kaptcha",text);
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
