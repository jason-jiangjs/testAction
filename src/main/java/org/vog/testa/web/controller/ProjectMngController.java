package org.vog.testa.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.vog.base.controller.BaseController;

import java.util.Map;

/**
 * 查询表及列的一览
 */
@Controller
public class ProjectMngController extends BaseController {

    /**
     * 转到项目一览画面
     */
    @RequestMapping(value = "/projList", method = RequestMethod.GET)
    public ModelAndView tologin(@RequestParam Map<String, String> params) {
        ModelAndView model = new ModelAndView();
        model.setViewName("proj_list");
        return model;
    }
}
