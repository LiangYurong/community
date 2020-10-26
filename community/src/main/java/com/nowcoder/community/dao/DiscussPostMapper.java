package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import java.util.*;

@Mapper
@Component
public interface DiscussPostMapper {

    /**
     * 一个动态sql语句
     * 首页功能。查询到所有的帖子，不包括拉黑的帖子。
     * @param userId 将来在查询"我的帖子"，可以根据userId查询到该用户的所有帖子。userId==0，首页展示所有帖子，否则根据userId来展示特定用户的帖子。
     * @param offset 每一页起始行的行号
     * @param limit 设置一页展示多少个帖子
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    /**
     * 一个动态sql语句
     * 查询表里一共有多少条数据
     * @param userId 当userId为0，不拼接后面的条件。当开发个人主页的时候，就可以拼接后面的条件。
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId);
}
