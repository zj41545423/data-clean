package com.my.zhj.cloud.controller;

import com.my.zhj.cloud.springjpa.entity.DpmFunction;
import com.my.zhj.cloud.springjpa.entity.DpmImport;
import com.my.zhj.cloud.springjpa.entity.DpmUser;
import com.my.zhj.cloud.springjpa.entity.QDpmFunction;
import com.my.zhj.cloud.springjpa.repository.DpmFunctionRepository;
import com.my.zhj.cloud.springjpa.repository.DpmImportRepository;
import com.my.zhj.cloud.springjpa.repository.DpmIndTypeRepository;
import com.my.zhj.cloud.util.IndicatorUtil;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * Created by zhj on 2019/6/4.
 */
@Controller
@RequestMapping("/user")
public class DpmFunctionController {

    @Autowired
    DpmFunctionRepository dpmFunctionRepository;


    // 首页 ,查询函数列表
    @GetMapping("/function_{pageCurrent}_{pageSize}_{pageCount}")
    public String functionIndex(@PathVariable Integer pageCurrent,
                              @PathVariable Integer pageSize,
                              @PathVariable Integer pageCount,
                              @RequestParam(name = "funcName",required = false) String funcName,
                                @RequestParam(name = "funcDesc",required = false) String funcDesc,
                                @RequestParam(name = "funcStatus",required = false, defaultValue = "ALL") String funcStatus,
                              ModelMap model){

        if (pageSize == 0) pageSize = 16;


        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if(!StringUtils.isEmpty(funcName)){
            booleanBuilder.and(QDpmFunction.dpmFunction.funcCode.containsIgnoreCase(funcName));
        }
        if(!StringUtils.isEmpty(funcDesc)){
            booleanBuilder.and(QDpmFunction.dpmFunction.funcDesc.containsIgnoreCase(funcDesc));
        }
        if(!StringUtils.equals(funcStatus,"ALL")){
            booleanBuilder.and(QDpmFunction.dpmFunction.status.eq(funcStatus));
        }

        int rows = (int) dpmFunctionRepository.count(booleanBuilder);
        if (pageCount == 0) pageCount = rows % pageSize == 0 ? (rows / pageSize) : (rows / pageSize) + 1;


        Page<DpmFunction> funcs = dpmFunctionRepository.findAll(booleanBuilder,new PageRequest(pageCurrent - 1, pageSize, new Sort(Sort.Direction.DESC, "createdDate")));

        String pageHTML = IndicatorUtil.getPageContent("function_{pageCurrent}_{pageSize}_{pageCount}?funcName="+funcName+"&funcDesc="+funcDesc+"&funcStatus="+funcStatus, pageCurrent, pageSize, pageCount);
        model.addAttribute("pageHTML", pageHTML);

        model.addAttribute("list", funcs.getContent());
        return "function/index";
    }

    // 新增 函数
    @GetMapping("/functionCreate")
    public String functionCreate(Model model) {
        DpmFunction dpmFunction = new DpmFunction();
        dpmFunction.setFuncVersion(1);
        model.addAttribute("item",dpmFunction);
        return "function/functionEdit";
    }

    // 修改函数 - 编辑
    @GetMapping("/functionEdit")
    public String functionEditGet(@RequestParam(name = "id") Long id,Model model) {
        DpmFunction dpmFunction = dpmFunctionRepository.findById(id).get();
        model.addAttribute("item",dpmFunction);
        return "function/functionEdit";
    }


    // 修改函数-提交
    @PostMapping("/functionEdit")
    public String functionEditPost(DpmFunction function,HttpSession httpSession) {

        DpmUser user = (DpmUser) httpSession.getAttribute("user");
        function.setCreatedBy(user.getUserName());
        function.setCreatedDate(new Date());
        function.setFuncVersion(function.getFuncVersion()+1);
        dpmFunctionRepository.save(function);

        return "redirect:function_1_0_0";
    }

//
    // 删除导入类
    @ResponseBody
    @PostMapping("/functionDelete")
    public void functionDelete(@RequestParam(name = "id") Long id){
        dpmFunctionRepository.deleteById(id);
    }

}
