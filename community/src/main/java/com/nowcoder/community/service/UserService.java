package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    /**
     * 项目名(用于邮件激活)
     */
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 域名(用于邮件激活)
     */
    @Value("${community.path.domain}")
    private String domain;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }


    /**
     * 用户注册功能。需要用户自己输入账号，密码，邮箱。关于用户的其他数据，都在后台帮忙设置了。比如user的salt属性，比如user的初始头像，初始状态，
     * 1、传入的username，password，email不能为空。
     * 2、验证username、email是否被注册了。
     *
     * @param user
     * @return
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        if (null == user) {
            throw new IllegalArgumentException("user为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        //验证账号是否存在
        User user1 = userMapper.selectByName(user.getUsername());
        if (null != user1) {
            map.put("usernameMsg", "该账号已存在");
            return map;
        }
        //验证邮箱是否存在
        User user2 = userMapper.selectByEmail(user.getEmail());
        if (null != user2) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }

        //上述验证没问题，进行用户注册
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));//截取5位随机字符
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        //使用牛客网的1000个头像。%d是Random类随机生成的整数
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活码邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //http://localhost:8081/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    /**
     * 激活邮件功能，判断传入的userid与验证码是否符合。
     * @param userId
     * @param code
     * @return
     */
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 验证，看登录成功还是登录失败。
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    public Map<String,Object>  login(String username,String password, int expiredSeconds){
        Map<String,Object> map=new HashMap<>();

        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        //验证账号
        User user = userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","账号不存在");
            return map;
        }
        //验证状态
        if(user.getStatus()==0){ //0-注册没激活。1-注册且激活
            map.put("usernameMsg","账号没激活");
            return map;
        }
        //验证密码(将密码按照同样的规则加密，再去比较密码
        password=CommunityUtil.md5(password+user.getSalt());
        if(!password.equals(user.getPassword())){
            map.put("passwordMsg","密码错误");
            return map;
        }
        //以上没问题，则证明登录可以成功。应该生成登录凭证并保存到数据库。
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());

        return map;
    }

    /**
     * 退出登录，就把凭证的状态设置为失效即可
     * @param ticket
     */
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }

    /**
     * 根据ticket查找到对应的LoginTicket对象
     * @param ticket
     * @return
     */
    public LoginTicket findLoginTicketByTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }


    public int updateHeader(int userId,String headerUrl){
        return userMapper.updateHeader(userId,headerUrl);
    }

    public int updatePassword(int userId,String password){
        return userMapper.updatePassword(userId,password);
    }

}
