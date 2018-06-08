package org.vog.testa.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.vog.base.controller.BaseController;

import java.util.Map;

/**
 * 查询统计数据
 */
@Controller
public class StatisticMngController extends BaseController {


    /**
     * 转到统计数据画面
     */
    @RequestMapping(value = "/statistic_list", method = RequestMethod.GET)
    public ModelAndView tologin(@RequestParam Map<String, String> params) {
        ModelAndView model = new ModelAndView();
        model.setViewName("statistic");
        return model;
    }

}
