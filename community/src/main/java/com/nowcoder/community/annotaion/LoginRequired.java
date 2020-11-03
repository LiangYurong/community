package com.nowcoder.community.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 自定义注解。
 *
 * ElementType.METHOD代表该注解可写在方法之上
 *
 * 配置之后，直接在方法上面添加注解  @LoginRequired
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
