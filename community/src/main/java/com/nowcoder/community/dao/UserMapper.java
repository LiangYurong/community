package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserMapper {

    /**
     * 根据id查找user
     * @param id 用户id
     * @return
     */
    User selectById(int id);

    /**
     * 根据name查找user
     * @param username
     * @return
     */
    User selectByName(String username);

    /**
     * 根据email查找user
     * @param email
     * @return
     */
    User selectByEmail(String email);

    /**
     * 插入一个新的user
     * @param user
     * @return
     */
    int insertUser(User user);

    /**
     * 根据id更新用户的status
     * @param id
     * @param status
     * @return
     */
    int updateStatus(int id, int status);

    /**
     * 根据id更新用户的头像
     * @param id
     * @param headerUrl
     * @return
     */
    int updateHeader(int id, String headerUrl);

    /**
     * 根据id更新用户的password
     * @param id
     * @param password
     * @return
     */
    int updatePassword(int id, String password);
}
