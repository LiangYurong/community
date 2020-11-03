package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有对象信息，代替session对象。为什么不用session对象？
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users=new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    /**
     * 将ThreadLocal中的user清理掉，不然占用内存会越来越多
     */
    public void clear(){
        users.remove();
    }
}
