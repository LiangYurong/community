package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.dao.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = RunMyApplication.class)
public class TestApp {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelect() {
        User user = userMapper.selectById(1);
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {

        User user = new User();
        user.setUsername("aa");
        user.setPassword("11");
        user.setSalt("abc");
        user.setEmail("aa@qq.com");
        user.setHeaderUrl("aa.html");
        user.setCreateTime(new Date());
        int row = userMapper.insertUser(user);
        System.out.println(row);
    }

    @Test
    public void testUpdatePassword() {

        int i = userMapper.updatePassword(1, "11aa223");
        System.out.println(i);
        User user = userMapper.selectById(1);
        System.out.println(user);
    }


    @Test
    public void testSelectDiscussPosts() {

        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 3);
        for (DiscussPost d : list) {
            System.out.println(d);
        }

        int i = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(i);
    }
}
