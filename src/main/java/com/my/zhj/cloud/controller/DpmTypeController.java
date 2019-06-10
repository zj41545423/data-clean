package com.my.zhj.cloud.controller;

import com.my.zhj.cloud.common.Constants;
import com.my.zhj.cloud.springjpa.entity.DpmIndType;
import com.my.zhj.cloud.springjpa.entity.DpmUser;
import com.my.zhj.cloud.springjpa.repository.DpmIndTypeRepository;
import com.sun.tools.javac.code.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by zhj on 2019/6/4.
 */
@Controller
@RequestMapping("/user")
public class DpmTypeController {

    @Autowired
    DpmIndTypeRepository dpmIndTypeRepository;


    // 查询
    @GetMapping("/dpm_type")
    public String indexPage(ModelMap model){

        List<DpmIndType> dpmType = dpmIndTypeRepository.findAll(new Sort(Sort.Direction.DESC, "createdDate"));
        model.addAttribute("list", dpmType);
        return "dpmType/index";
    }

    //新增
    @GetMapping("/dpmTypeCreate")
    public String dpmTypeEdit(Model model) {
        model.addAttribute("item",new DpmIndType());
        return "dpmtype/typeEdit";
    }

    // 修改记录
    @GetMapping("/dpmTypeEdit")
    public String dpmTypeEditGet(Model model, DpmIndType indType) {
        if(indType.getId()!=null && indType.getId() != 0){
            DpmIndType item = dpmIndTypeRepository.findById(indType.getId()).get();
            model.addAttribute("item",item);
        }
        return "dpmtype/typeEdit";
    }

    // 修改 提交
    @PostMapping("/dpmTypeEdit")
    public String dpmTypeEditPost(DpmIndType dpmIndType,HttpSession httpSession) {

        DpmUser user = (DpmUser) httpSession.getAttribute("user");
        dpmIndType.setCreatedBy(user.getUserName());
        dpmIndType.setCreatedDate(new Date());
        dpmIndTypeRepository.save(dpmIndType);

        return "redirect:dpm_type";
    }


    @ResponseBody
    @PostMapping("/dpmTypeDelete")
    public void dpmTypeDelete(@RequestParam(name = "id") Long id){
        dpmIndTypeRepository.deleteById(id);
    }

}
