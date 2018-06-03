package org.vog.testa.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.vog.base.controller.BaseController;
import org.vog.base.model.mongo.BaseMongoMap;
import org.vog.common.Constants;
import org.vog.common.ErrorCode;
import org.vog.common.util.ApiResponseUtil;
import org.vog.common.util.DateTimeUtil;
import org.vog.common.util.StringUtil;
import org.vog.testa.service.ProjectService;
import org.vog.testa.service.UpdateHisService;
import org.vog.testa.service.UserService;
import org.vog.testa.web.login.CustomerUserDetails;

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

    @Autowired
    private UpdateHisService updateHisService;

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

    /**
     * 保存画面
     */
    @ResponseBody
    @PostMapping("/ajax/mng/saveProjInfo")
    public Map<String, Object> saveProjInfo(@RequestBody Map<String, Object> params) {
        Long adminId = (Long) request.getSession().getAttribute(Constants.KEY_USER_ID);
        if (adminId == null) {
            logger.error("用户未登录 sessionid={}", request.getSession().getId());
            return ApiResponseUtil.error(ErrorCode.S9004, "用户未登录");
        }

        Long projId = StringUtil.convertToLong(params.get("projId"));
        String projName = StringUtils.trimToNull((String) params.get("projName"));
        if (projName == null) {
            logger.warn("saveProjInfo 缺少参数 params={}", params.toString());
            return ApiResponseUtil.error(ErrorCode.W1001, "缺少参数，请填写完整再保存。");
        }
        BaseMongoMap dbObj = projectService.findProjectByName(projName);

        if (projId == 0) {
            // 新增
            if (dbObj != null) {
                logger.warn("saveProjInfo 项目名称重复 projName={}", projName);
                return ApiResponseUtil.error(ErrorCode.E5001, "该项目名称重复");
            }
            params.put("deleteFlg", false);
            params.put("creator", adminId);
            params.put("createdTime", DateTimeUtil.getNowTime());
            projectService.saveProject(projId, params);
        } else {
            // 修改
            if (dbObj != null && !projId.equals(dbObj.getLongAttribute("_id"))) {
                logger.warn("saveProjInfo 项目名称重复 projId={} projName={}", projId, projName);
                return ApiResponseUtil.error(ErrorCode.E5001, "该项目名称重复");
            }
            params.put("modifier", adminId);
            params.put("modifiedTime", DateTimeUtil.getNowTime());
            projectService.saveProject(projId, params);
        }
        return ApiResponseUtil.success();
    }

    /**
     * 删除画面
     */
    @ResponseBody
    @PostMapping("/ajax/mng/delProj")
    public Map<String, Object> deleteProj(@RequestBody Map<String, Object> params) {
        Long projId = StringUtil.convertToLong(params.get("projId"));
        if (projId == 0) {
            logger.warn("deleteProj 缺少参数 params={}", params.toString());
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

        projectService.removeProject(userId, projId);
        updateHisService.saveUpdateHis(userObj, projId, null, null);
        return ApiResponseUtil.success();
    }

}