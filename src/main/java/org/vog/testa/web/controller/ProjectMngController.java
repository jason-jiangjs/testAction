package org.vog.testa.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.vog.base.controller.BaseController;
import org.vog.common.util.StringUtil;
import org.vog.testa.service.PageService;
import org.vog.testa.service.ProjectService;

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
    private PageService pageService;

    /**
     * 查询所有项目
     */
    @ResponseBody
    @RequestMapping(value = "/ajax/getProjList", method = RequestMethod.GET)
    public Map<String, Object> getProjList(@RequestParam Map<String, String> params) {
//        Long userId = (Long) request.getSession().getAttribute(Constants.KEY_USER_ID);
//        if (userId == null || userId == 0) {
//            logger.error("用户未登录 sessionid={}", request.getSession().getId());
//            return ApiResponseUtil.error(ErrorCode.S9004, "用户未登录");
//        }
        int page = StringUtil.convertToInt(params.get("page"));
        int rows = StringUtil.convertToInt(params.get("rows"));

        Map<String, Object> data = new HashMap<>();
        data.put("total", projectService.countProjectList());
        data.put("rows", projectService.findProjectList(page, rows, true));
        return data;
    }

    /**
     * 转到指定项目的画面一览
     */
    @RequestMapping(value = "/page_list", method = RequestMethod.GET)
    public ModelAndView toPageList(@RequestParam String projId) {
        ModelAndView model = new ModelAndView();
        model.setViewName("page_list");
        model.addObject("projId", projId);
        return model;
    }

    /**
     * 查询指定项目的所有画面
     */
    @ResponseBody
    @RequestMapping(value = "/ajax/getProjPageList", method = RequestMethod.GET)
    public Map<String, Object> getProjPageList(@RequestParam Map<String, String> params) {
//        Long userId = (Long) request.getSession().getAttribute(Constants.KEY_USER_ID);
//        if (userId == null || userId == 0) {
//            logger.error("用户未登录 sessionid={}", request.getSession().getId());
//            return ApiResponseUtil.error(ErrorCode.S9004, "用户未登录");
//        }
        int page = StringUtil.convertToInt(params.get("page"));
        int rows = StringUtil.convertToInt(params.get("rows"));
        long projId = StringUtil.convertToLong(params.get("projId"));

        Map<String, Object> data = new HashMap<>();
        data.put("total", pageService.countPageList(projId));
        data.put("rows", pageService.findPageList(page, rows, projId));
        return data;
    }

}