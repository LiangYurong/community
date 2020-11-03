package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

/**
 * 登录凭证实体类。
 *
 * 这里不采用xml形式写sql，而是采用注解的方式写sql。
 * 采用注解的方式也是可以写动态sql的，需要添加<script></script>
 */
@Mapper
@Component
public interface LoginTicketMapper {

    /**
     * 用户登录成功之后需要插入一条凭证
     * @param loginTicket 需要插入的凭证实体类
     * @return
     */
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id") //代表自动生成主键。
    int insertLoginTicket(LoginTicket loginTicket);

    /**
     * 根据ticket查询哪个用户在登录或访问
     * @param ticket 登录凭证
     * @return 返回查到的登录凭证实体类
     */
    @Select({
            "select id,user_id,ticket,status,expired from login_ticket where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);


    /**
     * 修改登录凭证的状态。当用户退出之后，登录凭证应该失效。
     *
     * 一般不会删除数据，而是改状态即可。以后该条记录可能有用。
     */
    @Update({
            "update login_ticket set status = #{status} where ticket = #{ticket}"
    })
    int updateStatus(String ticket,int status);

}
