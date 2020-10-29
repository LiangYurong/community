package com.nowcoder.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 一个简单的demo程序。与本项目无关。
 */
@Controller
public class AlphaController {

    /**
     * 服务器向浏览器返回一个cookie
     * @param response
     * @return
     */
    @GetMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        //create Cookie
        Cookie cookie = new Cookie("code", "thisismycode");
        //设置Cookie生效的范围
        cookie.setPath("/community/index");
        //设置cookie生效时长为10分钟
        cookie.setMaxAge(60 * 10);
        response.addCookie(cookie);
        return "set cookie finish";
    }

    @GetMapping("cookie/get")
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }

    /**
     * 设置一个HttpSession
     * @param session
     * @return
     */
    @GetMapping("session/set")
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","yurong333");
        return "set session";
    }

    /**
     * 获取之前的HttpSession
     * @param session
     * @return
     */
    @GetMapping("session/get")
    @ResponseBody
    public String getSession(HttpSession session){
        Object id = session.getAttribute("id");
        Object name = session.getAttribute("name");
        return "get session"+id+" "+name;
    }
}
