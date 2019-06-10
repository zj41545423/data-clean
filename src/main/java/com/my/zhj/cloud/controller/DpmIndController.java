package com.my.zhj.cloud.controller;

import com.my.zhj.cloud.springjpa.entity.*;
import com.my.zhj.cloud.springjpa.repository.DpmIndTypeRepository;
import com.my.zhj.cloud.springjpa.repository.DpmIndicatorRepository;
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
import java.util.List;

/**
 * Created by zhj on 2019/6/4.
 */
@Controller
@RequestMapping("/user")
public class DpmIndController {

    @Autowired
    DpmIndTypeRepository dpmIndTypeRepository;

    @Autowired
    DpmIndicatorRepository dpmIndicatorRepository;


    // 首页 ,查询指标列表
    @GetMapping("/indicator_{pageCurrent}_{pageSize}_{pageCount}")
    public String indicatorIndex(@PathVariable Integer pageCurrent,
                              @PathVariable Integer pageSize,
                              @PathVariable Integer pageCount,
                              @RequestParam(name = "indicatorKey",required = false) String indicatorKey,
                              @RequestParam(name = "indicatorDesc",required = false) String indicatorDesc,
                              @RequestParam(name = "status",required = false, defaultValue = "ALL") String status,
                              @RequestParam(name = "type",required = false, defaultValue = "ALL") String type,
                              @RequestParam(name = "grade",required = false) Integer grade,
                              ModelMap model){

        if (pageSize == 0) pageSize = 16;

        List<DpmIndType> types = dpmIndTypeRepository.findAll();
        model.addAttribute("indTypes", types);


        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if(!StringUtils.isEmpty(indicatorKey)){
            booleanBuilder.and(QDpmIndicator.dpmIndicator.indicatorKey.containsIgnoreCase(indicatorKey));
        }
        if(!StringUtils.isEmpty(indicatorDesc)){
            booleanBuilder.and(QDpmIndicator.dpmIndicator.indicatorDesc.containsIgnoreCase(indicatorDesc));
        }
        if(!StringUtils.equals(status,"ALL")){
            booleanBuilder.and(QDpmIndicator.dpmIndicator.status.eq(status));
        }
        if(!StringUtils.equals(type,"ALL")){
            booleanBuilder.and(QDpmIndicator.dpmIndicator.type.eq(type));
        }
        if(grade!=null){
            booleanBuilder.and(QDpmIndicator.dpmIndicator.grade.eq(grade));
        }

        int rows = (int) dpmIndicatorRepository.count(booleanBuilder);
        if (pageCount == 0) pageCount = rows % pageSize == 0 ? (rows / pageSize) : (rows / pageSize) + 1;


        Page<DpmIndicator> indicators = dpmIndicatorRepository.findAll(booleanBuilder, new PageRequest(pageCurrent - 1, pageSize, new Sort(Sort.Direction.DESC, "createdDate")));

        String pageHTML = IndicatorUtil.getPageContent("function_{pageCurrent}_{pageSize}_{pageCount}?indicatorKey="+indicatorKey+"&indicatorDesc="+indicatorDesc+"&status="+status+"&type="+type+"&grade="+grade, pageCurrent, pageSize, pageCount);
        model.addAttribute("pageHTML", pageHTML);

        model.addAttribute("list", indicators.getContent());
        return "indicator/index";
    }

    // 新增 指标
    @GetMapping("/indicatorCreate")
    public String indicatorCreate(Model model) {
        DpmIndicator dpmIndicator = new DpmIndicator();
        dpmIndicator.setVersion(1);
        model.addAttribute("item",dpmIndicator);
        List<DpmIndType> types = dpmIndTypeRepository.findAll();
        model.addAttribute("indTypes", types);
        return "indicator/indicatorEdit";
    }

    // 修改函数 - 编辑
    @GetMapping("/indicatorEdit")
    public String indicatorEditGet(@RequestParam(name = "id") Long id,Model model) {
        DpmIndicator dpmIndicator = dpmIndicatorRepository.findById(id).get();
        model.addAttribute("item",dpmIndicator);

        List<DpmIndType> types = dpmIndTypeRepository.findAll();
        model.addAttribute("indTypes", types);
        return "indicator/indicatorEdit";
    }


    // 修改函数-提交
    @PostMapping("/indicatorEdit")
    public String indicatorEditPost(DpmIndicator indicator,HttpSession httpSession) {

        DpmUser user = (DpmUser) httpSession.getAttribute("user");
        indicator.setCreatedBy(user.getUserName());
        indicator.setCreatedDate(new Date());
        indicator.setVersion(indicator.getVersion()+1);
        dpmIndicatorRepository.save(indicator);

        return "redirect:indicator_1_0_0";
    }

//
    // 删除导入类
    @ResponseBody
    @PostMapping("/indicatorDelete")
    public void indicatorDelete(@RequestParam(name = "id") Long id){
        dpmIndicatorRepository.deleteById(id);
    }

}
