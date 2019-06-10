package com.my.zhj.cloud.controller;

import com.my.zhj.cloud.springjpa.entity.DpmUser;
import com.my.zhj.cloud.springjpa.entity.QDpmUser;
import com.my.zhj.cloud.springjpa.repository.DpmUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhj on 2019/6/4.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private DpmUserRepository dpmUserRepository;

    @Autowired
    private HttpSession httpSession;

    @GetMapping("/login")
    public String loginPage(ModelMap map){
        return "login";
    }


    /**
     * 登录
     *
     * @param
     * @param model
     * @param
     * @return
     */
    @PostMapping("/login")
    public String loginPost(DpmUser user, Model model) {
        Iterator<DpmUser> iterator = dpmUserRepository.findAll(QDpmUser.dpmUser.userName.eq(user.getUserName()).and(QDpmUser.dpmUser.password.eq(user.getPassword()))).iterator();
        if (iterator.hasNext()) {
            httpSession.setAttribute("user", iterator.next());
            return "redirect:dpm_type";
        } else {
            model.addAttribute("error", "用户名或密码错误，请重新登录！");
            return "login";
        }
    }

    // 查询用户列表
    @GetMapping("/userList")
    public String userList(ModelMap model){

        List<DpmUser> list = dpmUserRepository.findAll();
        model.addAttribute("list", list);
        return "user/index";
    }


    //新增
    @GetMapping("/userCreate")
    public String userCreate(Model model) {
        model.addAttribute("item",new DpmUser());
        return "user/userEdit";
    }

    // 修改记录
    @GetMapping("/userEdit")
    public String userEditGet(Model model, @RequestParam(name = "id") Long id) {
        DpmUser dpmUser = dpmUserRepository.findById(id).get();
        model.addAttribute("item",dpmUser);

        return "user/userEdit";
    }

    // 修改 提交
    @PostMapping("/userEdit")
    public String userEditPost(DpmUser user,HttpSession httpSession) {

        DpmUser sUser = (DpmUser) httpSession.getAttribute("user");
        user.setCreatedBy(sUser.getUserName());
        user.setCreatedDate(new Date());
        dpmUserRepository.save(user);

        return "redirect:userList";
    }

    @ResponseBody
    @PostMapping("/userDelete")
    public void userDelete(@RequestParam(name = "id") Long id){
        dpmUserRepository.deleteById(id);
    }

}

