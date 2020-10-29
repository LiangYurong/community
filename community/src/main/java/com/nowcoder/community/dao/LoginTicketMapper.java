package com.nowcoder.community.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface LoginTicketMapper {

    //用户登录成功之后需要插入一条凭证
}
