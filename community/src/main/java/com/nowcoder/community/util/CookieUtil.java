package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 一个工具类，从cookie中获取到一些值，
 */
public class CookieUtil {
    /**
     *
     * @param request 请求
     * @param name 根据name从Cookie中找到对应的值
     * @return 返回结果
     */
    public static String getValue(HttpServletRequest request,String name){
        if(request==null || name == null){
            throw new IllegalArgumentException("参数为空");
        }
        //获取到的是一个数组，因此需要遍历找到
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie:cookies){
                if(cookie.getName().equalsIgnoreCase(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
