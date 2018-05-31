package org.vog.testa.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.vog.base.controller.BaseController;
import org.vog.common.util.StringUtil;
import org.vog.testa.service.ProjectService;
import org.vog.testa.service.UserService;

import java.util.HashMap;
import java.util.Map;

/**
 * 查询项目一览
 */
@Controller
public class ProjectMngController extends BaseController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    /**
     * 查询所有项目
     */
    @ResponseBody
    @RequestMapping(value = "/ajax/getProjList", method = RequestMethod.GET)
    public Map<String, Object> getProjList(@RequestParam Map<String, String> params) {
        int page = StringUtil.convertToInt(params.get("page"));
        int rows = StringUtil.convertToInt(params.get("rows"));

        Map<String, Object> data = new HashMap<>();
        data.put("total", projectService.countProjectList());
        data.put("rows", projectService.findProjectList(page, rows, true));
        return data;
    }

}