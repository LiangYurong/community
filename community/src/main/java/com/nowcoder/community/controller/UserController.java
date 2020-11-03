package com.nowcoder.community.controller;

import com.nowcoder.community.annotaion.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * 当前业务属于User方面的，因此命名为UserController。
 *
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 用户设置页面
     * @return
     */
    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(){
        return "site/setting";
    }

    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","还没有选择图片");
            return "site/setting";
        }
        //不能将上传的图片名字直接填入，需要给个随机不重复的名字
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确");
            return "site/setting";
        }
        //生成随机的文件名
        filename= CommunityUtil.generateUUID()+suffix;
        //存储文件
        File dest=new File(uploadPath+"/"+filename);
        try {
            //将文件存储到本地路径
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常",e);
        }
        //更新头像的路径(web路径，非本地路径)：http://localhost:8081/comunity/user/header/xxx.png
        User user=hostHolder.getUser();
        String headerUrl=domain+contextPath+"/user/header/"+filename;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    /**
     * 获取头像的二进制数据.
     * 这个方法在哪里调用来着？？
     */
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException {
        //本地服务器存放头像的路径
        fileName=uploadPath+"/"+fileName;
        //图片后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应文件
        response.setContentType("/image/"+suffix);
        OutputStream os = null;
        FileInputStream fis = null;
        try {
            os = response.getOutputStream();
            fis = new FileInputStream(fileName);
            byte[] buffer=new byte[1024];
            int b=0;
            while ((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("error","读取头像失败"+e.getMessage());
        }finally {
            fis.close();
            os.close();;
        }
    }


    @PostMapping("/updatePassword")
    public String updatePassword(String oldPassword,String newPasswordOne,String newPasswordTwo,Model model){

        if(!newPasswordOne.equals(newPasswordTwo)){
            model.addAttribute("errorPassword","输入的新密码不一致");
            return "site/setting";
        }

        User user=hostHolder.getUser();
        if(!CommunityUtil.md5(oldPassword+user.getSalt()).equals(user.getPassword())){
            model.addAttribute("error","输入的旧密码不正确");
            return "site/setting";
        }

        String newPass=CommunityUtil.md5(newPasswordOne+user.getSalt());
        userService.updatePassword(user.getId(),newPass);
        return "redirect:/index";
    }
}
