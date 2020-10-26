package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页展示。每页展示10条数据。可以展示当前页的前后两条数据。
 */
@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;


    /**
     * 首页展示。
     * 一篇帖子需要展示内容和帖子所有者。（根据DiscussPost的userId，可以找到具体的User类）
     * @param model
     * @return
     */
    @GetMapping("/index")
    public String getIndexPage(Model model, Page page){
        //首页，查询出有多少帖子
        int rows=discussPostService.findDiscussPostRows(0);
        page.setRows(rows);
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if(list!=null){
            for(DiscussPost post:list){
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                if(user!=null){
                    map.put("user",user);
                }else {
                    map.put("user",new User());
                }

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        //方法调用前，SpringMVC会自动实例化Model和page,并将Page自动注入到Model，因此在thymeleaf中可以直接访问Page对象中的数据。
        //model.addAttribute("page",page);
        return "index";
    }
}
