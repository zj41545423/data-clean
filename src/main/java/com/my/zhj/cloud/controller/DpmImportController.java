package com.my.zhj.cloud.controller;

import com.my.zhj.cloud.springjpa.entity.DpmImport;
import com.my.zhj.cloud.springjpa.entity.DpmUser;
import com.my.zhj.cloud.springjpa.repository.DpmImportRepository;
import com.my.zhj.cloud.springjpa.repository.DpmIndTypeRepository;
import com.my.zhj.cloud.util.IndicatorUtil;
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
public class DpmImportController {

    @Autowired
    DpmIndTypeRepository dpmIndTypeRepository;

    @Autowired
    DpmImportRepository dpmImportRepository;


    // 首页 ,查询导入类
    @GetMapping("/import_{pageCurrent}_{pageSize}_{pageCount}")
    public String importIndex(@PathVariable Integer pageCurrent,
                              @PathVariable Integer pageSize,
                              @PathVariable Integer pageCount,
                              ModelMap model){

        if (pageSize == 0) pageSize = 16;

        int rows = (int) dpmImportRepository.count();
        if (pageCount == 0) pageCount = rows % pageSize == 0 ? (rows / pageSize) : (rows / pageSize) + 1;


        Page<DpmImport> imports = dpmImportRepository.findAll(new PageRequest(pageCurrent-1, pageSize, new Sort(Sort.Direction.DESC, "createdDate")));

        String pageHTML = IndicatorUtil.getPageContent("import_{pageCurrent}_{pageSize}_{pageCount}", pageCurrent, pageSize, pageCount);
        model.addAttribute("pageHTML", pageHTML);

        model.addAttribute("list", imports.getContent());
        return "import/index";
    }

    // 新增 导入类
    @GetMapping("/importCreate")
    public String importEditGet(Model model) {
        model.addAttribute("item",new DpmImport());
        return "import/importEdit";
    }

    // 修改提交导入类
    @GetMapping("/importEdit")
    public String importEditGet(@RequestParam(name = "id") Long id,Model model) {
        DpmImport dpmImport = dpmImportRepository.findById(id).get();
        model.addAttribute("item",dpmImport);
        return "import/importEdit";
    }


    // 修改导入类
    @PostMapping("/importEdit")
    public String importEditPost(DpmImport imports,HttpSession httpSession) {

        DpmUser user = (DpmUser) httpSession.getAttribute("user");
        imports.setCreatedBy(user.getUserName());
        imports.setCreatedDate(new Date());
        dpmImportRepository.save(imports);

        return "redirect:import_1_0_0";
    }


    // 删除导入类
    @ResponseBody
    @PostMapping("/importDelete")
    public void dpmTypeDelete(@RequestParam(name = "id") Long id){
        dpmImportRepository.deleteById(id);
    }

}
