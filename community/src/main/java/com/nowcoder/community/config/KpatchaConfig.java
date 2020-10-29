package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 声明为配置类，启动的时候会自动扫描该类
 */
@Configuration
public class KpatchaConfig {
    //将会被IOC容器扫描冰杯注册成Bean
    @Bean
    public Producer kaptchaProducer(){
        Properties properties=new Properties();
        //设置属性
        properties.setProperty("kaptcha.image.width","100");
        properties.setProperty("kaptcha.image.height","40");
        properties.setProperty("kaptcha.textproducer.front.size","32");
        properties.setProperty("kaptcha.textproducer.front.color","0,0,0");
        properties.setProperty("kaptcha.textproducer.char.string","0123456789ZXCVBNMASDFGHJKLQWERTYUIOP"); //从这些字符中随机生成
        properties.setProperty("kaptcha.textproducer.char.length","4");//随机生成4个字符
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise"); //设置噪声干扰。
        DefaultKaptcha kaptcha=new DefaultKaptcha();
        Config config=new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
