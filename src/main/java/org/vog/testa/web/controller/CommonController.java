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
public class CommonController extends BaseController {


    /**
     * 转到登录画面
     * TODO-- 期待更好方案，目前是因为AuthenticationFailureHandlerImpl要传参数到页面
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ModelAndView tologin(@RequestParam Map<String, String> params) {
        ModelAndView model = new ModelAndView();
        model.setViewName("index");
        model.addObject("errMsg", params.get("msg"));
        return model;
    }

}
