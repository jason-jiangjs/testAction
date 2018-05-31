package org.vog.testa.web.controller;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.vog.base.controller.BaseController;
import org.vog.base.model.mongo.BaseMongoMap;
import org.vog.common.util.StringUtil;
import org.vog.testa.service.PageService;
import org.vog.testa.service.TestItemService;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试项目管理
 */
@Controller
public class TestItemMngController extends BaseController {

    @Autowired
    private PageService pageService;

    @Autowired
    private TestItemService itemService;

    /**
     * 转到指定项目的画面一览
     */
    @RequestMapping(value = "/item_list", method = RequestMethod.GET)
    public ModelAndView toPageList(@RequestParam String pageId) {
        ModelAndView model = new ModelAndView();
        model.setViewName("item_list");
        BaseMongoMap pageObj = pageService.findPageById(NumberUtils.toLong(pageId));
        if (pageObj != null) {
            //
            model.addObject("projId", pageObj.getLongAttribute("projId"));
        }
        model.addObject("pageId", pageId);
        model.addObject("pageName", "test");
        return model;
    }

    /**
     * 查询指定表的定义
     */
    @ResponseBody
    @RequestMapping(value = "/ajax/getPageItemList", method = RequestMethod.GET)
    public Map<String, Object> getProjPageList(@RequestParam Map<String, String> params) {
//        Long userId = (Long) request.getSession().getAttribute(Constants.KEY_USER_ID);
//        if (userId == null || userId == 0) {
//            logger.error("用户未登录 sessionid={}", request.getSession().getId());
//            return ApiResponseUtil.error(ErrorCode.S9004, "用户未登录");
//        }
        long pageId = StringUtil.convertToLong(params.get("pageId"));

        Map<String, Object> data = new HashMap<>();
        data.put("total", itemService.countTestItemList(pageId));
        data.put("rows", itemService.findTestItemList(pageId));
        return data;
    }

}