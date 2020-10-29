package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * 提供两个方法，提供给注册功能用
 */
public class CommunityUtil {

    /**
     * @return 生成随机字符串。如果有-，替换成空字符串。
     */
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * @param key 原密码+一个随机字符串。
     * @return md5加密后的字符串
     */
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        //将传入的结果加密成一个16进制的字符串。
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
