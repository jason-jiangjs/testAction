package org.vog.testa.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.vog.base.controller.BaseController;
import org.vog.base.model.mongo.BaseMongoMap;
import org.vog.common.Constants;
import org.vog.common.ErrorCode;
import org.vog.common.util.ApiResponseUtil;
import org.vog.common.util.DateTimeUtil;
import org.vog.common.util.StringUtil;
import org.vog.testa.service.PageService;
import org.vog.testa.service.UpdateHisService;
import org.vog.testa.service.UserService;
import org.vog.testa.web.login.CustomerUserDetails;

import java.util.HashMap;
import java.util.Map;

/**
 * 查询项目一览
 */
@Controller
public class PageMngController extends BaseController {

    @Autowired
    private PageService pageService;

    @Autowired
    private UserService userService;

    @Autowired
    private UpdateHisService updateHisService;

    /**
     * 转到指定项目的画面一览
     */
    @GetMapping("/page_list")
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
    @GetMapping("/ajax/getProjPageList")
    public Map<String, Object> getProjPageList(@RequestParam Map<String, String> params) {
        int page = StringUtil.convertToInt(params.get("page"));
        int rows = StringUtil.convertToInt(params.get("rows"));
        long projId = StringUtil.convertToLong(params.get("projId"));

        Map<String, Object> data = new HashMap<>();
        data.put("total", pageService.countPageList(projId));
        data.put("rows", pageService.findPageList(page, rows, projId));
        return data;
    }

    /**
     * 保存画面
     */
    @ResponseBody
    @PostMapping("/ajax/mng/savePageInfo")
    public Map<String, Object> savePageInfo(@RequestBody Map<String, Object> params) {
        Long adminId = (Long) request.getSession().getAttribute(Constants.KEY_USER_ID);
        if (adminId == null) {
            logger.error("用户未登录 sessionid={}", request.getSession().getId());
            return ApiResponseUtil.error(ErrorCode.S9004, "用户未登录");
        }

        Long projId = StringUtil.convertToLong(params.get("projId"));
        if (projId == 0) {
            logger.warn("savePageInfo 缺少参数projId params={}", params.toString());
            return ApiResponseUtil.error(ErrorCode.W1001, "缺少参数，请填写完整再保存。");
        }
        params.put("projId", projId);

        Long pageId = StringUtil.convertToLong(params.get("pageId"));
        String pageName = StringUtils.trimToNull((String) params.get("pageName"));
        if (pageName == null) {
            logger.warn("savePageInfo 缺少参数 params={}", params.toString());
            return ApiResponseUtil.error(ErrorCode.W1001, "缺少参数，请填写完整再保存。");
        }
        BaseMongoMap dbObj = pageService.findPageByName(projId, pageName);

        int codeCnt = StringUtil.convertToInt(params.get("codeCnt"));
        if (codeCnt == 0) {
            params.put("codeCnt", null);
        } else {
            params.put("codeCnt", codeCnt);
        }
        int diffiLevel = StringUtil.convertToInt(params.get("diffiLevel"));
        if (diffiLevel == 0) {
            params.put("diffiLevel", null);
        } else {
            params.put("diffiLevel", diffiLevel);
        }

        params.remove("pageId");

        if (pageId == 0) {
            // 新增
            if (dbObj != null) {
                logger.warn("savePageInfo 画面名称重复 pageName={}", pageName);
                return ApiResponseUtil.error(ErrorCode.E5001, "该画面名称重复");
            }
            params.put("deleteFlg", false);
            params.put("creator", adminId);
            params.put("createdTime", DateTimeUtil.getNowTime());
            pageService.savePage(pageId, params);
        } else {
            // 修改
            if (dbObj != null && !pageId.equals(dbObj.getLongAttribute("_id"))) {
                logger.warn("savePageInfo 画面名称重复 pageId={} pageName={}", pageId, pageName);
                return ApiResponseUtil.error(ErrorCode.E5001, "该画面名称重复");
            }
            params.put("modifier", adminId);
            params.put("modifiedTime", DateTimeUtil.getNowTime());
            pageService.savePage(pageId, params);
        }
        return ApiResponseUtil.success();
    }

    /**
     * 删除画面
     */
    @ResponseBody
    @PostMapping("/ajax/mng/delPage")
    public Map<String, Object> deletePage(@RequestBody Map<String, Object> params) {
        Long projId = StringUtil.convertToLong(params.get("projId"));
        Long pageId = StringUtil.convertToLong(params.get("pageId"));
        if (pageId == 0) {
            logger.warn("deletePage 缺少参数 params={}", params.toString());
            return ApiResponseUtil.error(ErrorCode.W1001, "缺少参数，请选择后再操作。");
        }
        Long userId = (Long) request.getSession().getAttribute(Constants.KEY_USER_ID);
        if (userId == null || userId == 0) {
            logger.error("用户未登录 sessionid={}", request.getSession().getId());
            return ApiResponseUtil.error(ErrorCode.S9004, "用户未登录");
        }
        CustomerUserDetails userObj = (CustomerUserDetails) ((Authentication) request.getUserPrincipal()).getPrincipal();
        if (userObj == null) {
            logger.error("用户未登录 sessionid={}", request.getSession().getId());
            return ApiResponseUtil.error(ErrorCode.S9004, "用户未登录");
        }

//        if (userObj.getIntAttribute("role") != 9) {
//            logger.warn("deletePage 用户无权限 userId={}", userId);
//            return ApiResponseUtil.error(ErrorCode.E5001, "该登录用户无删除权限 userId={}", userId);
//        }

        pageService.removePage(userId, pageId);
        updateHisService.saveUpdateHis(userObj, projId, null, null);
        return ApiResponseUtil.success();
    }
}