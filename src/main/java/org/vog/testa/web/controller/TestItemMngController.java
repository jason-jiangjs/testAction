package org.vog.testa.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
import org.vog.testa.service.TestItemService;
import org.vog.testa.service.UpdateHisService;
import org.vog.testa.service.UserService;
import org.vog.testa.web.login.CustomerUserDetails;

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

    @Autowired
    private UserService userService;

    @Autowired
    private UpdateHisService updateHisService;

    private static Map<Long, Long> itemInEdit = new HashMap<>();

    /**
     * 转到测试项目一览画面
     */
    @RequestMapping(value = "/item_list", method = RequestMethod.GET)
    public ModelAndView toItemList(@RequestParam String pageId) {
        ModelAndView model = new ModelAndView();
        model.setViewName("item_list");
        BaseMongoMap pageObj = pageService.findPageById(NumberUtils.toLong(pageId));
        if (pageObj == null) {
            logger.warn("toItemList 该画面未定义 pageId={}", pageId);
            return model;
        }
        long projId = pageObj.getLongAttribute("projId");
        model.addObject("projId", projId);
        model.addObject("pageId", pageId);
        model.addObject("pageName", pageObj.getStringAttribute("pageName"));

        CustomerUserDetails userObj = (CustomerUserDetails) ((Authentication) request.getUserPrincipal()).getPrincipal();
        if (userObj == null) {
            logger.error("toItemList 用户未登录 sessionid={}", request.getSession().getId());
            return model;
        }
        model.addObject("editable", userService.checkUserRole(userObj.getId(), projId) ? 1 : 0);
        return model;
    }

    /**
     * 查询测试项目
     */
    @ResponseBody
    @RequestMapping(value = "/ajax/getPageItemList", method = RequestMethod.GET)
    public Map<String, Object> getProjPageList(@RequestParam Map<String, String> params) {
        long pageId = StringUtil.convertToLong(params.get("pageId"));

        Map<String, Object> data = new HashMap<>();
        data.put("total", itemService.countTestItemList(pageId));
        data.put("rows", itemService.findTestItemList(pageId));
        return data;
    }

    /**
     * 检查指定表是否已在被编辑，或者自从上次查看后被人编辑保存过了
     * 返回code:  1表示有人正在编辑　2表示已被保存过，需要刷新
     */
    @ResponseBody
    @RequestMapping(value = "/ajax/chkItemEditable", method = RequestMethod.POST)
    public Map<String, Object> chkItemEditable(@RequestParam Map<String, String> params) {
        Long userId = (Long) request.getSession().getAttribute(Constants.KEY_USER_ID);

        long itemId = StringUtil.convertToLong(params.get("itemId"));
        if (itemId == 0) {
            logger.warn("chkItemEditable 缺少参数itemId");
            return ApiResponseUtil.error(ErrorCode.W1001, "缺少参数itemId");
        }

        BaseMongoMap itemMap = itemService.findTestItem(itemId);
        if (itemMap == null || itemMap.isEmpty()) {
            // 表不存在
            logger.warn("chkItemEditable 测试项不存在 itemId={}, userId={}", itemId, userId);
            return ApiResponseUtil.error(ErrorCode.E5101, "指定的测试项不存在 itemId={}", itemId);
        }

        Long currEditorId = itemMap.getLongAttribute("currEditorId");
        if (currEditorId == 0 || currEditorId.equals(userId)) {
            // 没人在编辑／或是自己在编辑(这种情况应该是出错了)
            // 再判断自从打开后是否被修改过
            long lastUpd = StringUtil.convertToLong(params.get("lastUpd"));
            long newUpd = itemMap.getLongAttribute("modifiedTime");
            if ((lastUpd == 0 && newUpd > 0) || (lastUpd > 0 && newUpd > 0 && lastUpd < newUpd)) {
                // 已经被编辑过了
                long modifier = itemMap.getLongAttribute("modifier");
                if (modifier == 0) {
                    return ApiResponseUtil.error(2, "该项已经被修改过了，需要重新加载。" );
                }
                BaseMongoMap userMap = userService.getUserByIId(modifier);
                if (userMap == null) {
                    logger.warn("chkItemEditable 用户不存在 modifier={}", modifier);
                    return ApiResponseUtil.error(2, "该项已经被用户(id={})修改过了，需要重新加载。", modifier);
                } else  {
                    return ApiResponseUtil.error(2, "该项已经被{}(id={})修改过了，需要重新加载。", userMap.getStringAttribute("userName"), modifier);
                }
            }

            itemService.setEditStatus(itemId, userId, DateTimeUtil.getNowTime());
            return ApiResponseUtil.success();
        }

        BaseMongoMap userMap = userService.getUserByIId(currEditorId);
        if (userMap == null) {
            logger.warn("chkItemEditable 用户不存在 currEditorId={}", currEditorId);
            return ApiResponseUtil.error(ErrorCode.E5011, null);
        }
        if (userMap.getIntAttribute("status") != 1) {
            logger.warn("chkItemEditable 用户状态异常 currEditorId={}, status={}", currEditorId, userMap.getIntAttribute("status"));
            return ApiResponseUtil.error(ErrorCode.E5012, userMap.getStringAttribute("userName") + "(" + userMap.getStringAttribute("userId") + ")");
        }

        // 已在编辑状态
        return ApiResponseUtil.error(1, userMap.getStringAttribute("userName") + "(" + userMap.getStringAttribute("userId") + ") 正在编辑该表，<br/>去催催吧。" );
    }

    /**
     * 保存测试项目
     */
    @ResponseBody
    @PostMapping("/ajax/saveTestItem")
    public Map<String, Object> saveTestItem(@RequestBody Map<String, Object> params) {
        Long projId = (Long) request.getSession().getAttribute("_projId");
        if (projId == 0) {
            logger.warn("saveTestItem 缺少参数 params={}", params.toString());
            return ApiResponseUtil.error(ErrorCode.W1001, "缺少参数，请选择后再操作。");
        }
        Long userId = (Long) request.getSession().getAttribute(Constants.KEY_USER_ID);
        CustomerUserDetails userObj = (CustomerUserDetails) ((Authentication) request.getUserPrincipal()).getPrincipal();

//        if (userObj.getIntAttribute("role") != 9) {
//            logger.warn("deletePage 用户无权限 userId={}", userId);
//            return ApiResponseUtil.error(ErrorCode.E5001, "该登录用户无删除权限 userId={}", userId);
//        }

        // 获取item id，新增项目时也会有id
        Long itemId = StringUtil.convertToLong(params.get("itemId"));
        BaseMongoMap itemMap = null;
        if (itemId != 0) {
            itemMap = itemService.findTestItem(itemId);
            if (itemMap == null) {
                // 表不存在
                logger.warn("saveTestItem 测试项不存在 itemId={}, userId={}", itemId, userId);
                return ApiResponseUtil.error(ErrorCode.E5101, "指定的测试项不存在 itemId={}", itemId);
            }
        }
        Long conflictUserId = itemInEdit.get(itemId);
        if (conflictUserId != null) {
            logger.warn("saveTestItem 有其他用户正在操作 itemId={}, userId={}, conflictUserId={}", itemId, userId, conflictUserId);
            BaseMongoMap userMap = userService.getUserByIId(conflictUserId);
            if (userMap == null) {
                logger.warn("saveTestItem 用户不存在 itemId={}, currEditorId={} userId={}", itemId, conflictUserId, userId);
            } else {
                logger.warn("saveTestItem {}正在编辑该项数据 itemId={}, currEditorId={} userId={}", userMap.getStringAttribute("userName"), itemId, conflictUserId, userId);
                return ApiResponseUtil.error(ErrorCode.E5103, "{}正在编辑，请稍后再试。 itemId={}", userMap.getStringAttribute("userName"), itemId);
            }
        }

        // 然后检查是否被编辑过（不允许同时打开编辑）

        // 其他参数
        String itemType = StringUtils.trimToEmpty((String) params.get("itemType"));
        if (itemType.equals("saveTestResult")) {
            if (StringUtils.isBlank(itemMap.getStringAttribute("testDate"))) {
                params.put("testDate", DateTimeUtil.getNow(DateTimeUtil.DEFAULT_DATE_FORMAT));
            }
            if (StringUtils.isBlank(itemMap.getStringAttribute("tester"))) {
                params.put("tester", userObj.getUsername());
            }
        }
        if (itemType.equals("saveConfirmResult")) {
            if (StringUtils.isBlank(itemMap.getStringAttribute("cfmDate"))) {
                params.put("cfmDate", DateTimeUtil.getNow(DateTimeUtil.DEFAULT_DATE_FORMAT));
            }
            if (StringUtils.isBlank(itemMap.getStringAttribute("confirmer"))) {
                params.put("confirmer", userObj.getUsername());
            }
        }
        if (itemType.equals("insert")) {
            // 插入数据，麻烦了，要对现有数据重新排列顺序，并保存
            if (itemType != null) {

            }

        }

        params.remove("itemId");
        params.remove("itemType");
        itemService.setEditStatus(itemId, null, null);
        itemService.saveTestItem(userId, itemId, params);
        updateHisService.saveItemUpdateHis(userObj, "保存测试条件", projId, itemId, params);
        return ApiResponseUtil.success();
    }

    /**
     * 删除测试项目
     */
    @ResponseBody
    @PostMapping("/ajax/delTestItem")
    public Map<String, Object> delTestItem(@RequestParam Map<String, Object> params) {
        Long projId = (Long) request.getSession().getAttribute("_projId");
        if (projId == 0) {
            logger.warn("delTestItem 缺少参数 params={}", params.toString());
            return ApiResponseUtil.error(ErrorCode.W1001, "缺少参数，请选择后再操作。");
        }
        Long userId = (Long) request.getSession().getAttribute(Constants.KEY_USER_ID);
        CustomerUserDetails userObj = (CustomerUserDetails) ((Authentication) request.getUserPrincipal()).getPrincipal();

//        if (userObj.getIntAttribute("role") != 9) {
//            logger.warn("deletePage 用户无权限 userId={}", userId);
//            return ApiResponseUtil.error(ErrorCode.E5001, "该登录用户无删除权限 userId={}", userId);
//        }

        // 获取item id
        long itemId = StringUtil.convertToLong(params.get("itemId"));
        if (itemId == 0) {
            logger.warn("delTestItem 缺少参数itemId");
            return ApiResponseUtil.error(ErrorCode.W1001, "缺少参数itemId");
        }
        BaseMongoMap itemMap = itemService.findTestItem(itemId);
        if (itemMap == null) {
            // 表不存在
            logger.warn("delTestItem 测试项不存在 itemId={}, userId={}", itemId, userId);
            return ApiResponseUtil.error(ErrorCode.E5101, "指定的测试项不存在 itemId={}", itemId);
        }

        Long conflictUserId = itemInEdit.get(itemId);
        if (conflictUserId != null) {
            logger.warn("delTestItem 有其他用户正在操作 itemId={}, userId={}, conflictUserId={}", itemId, userId, conflictUserId);
            BaseMongoMap userMap = userService.getUserByIId(conflictUserId);
            if (userMap == null) {
                logger.warn("delTestItem 用户不存在 itemId={}, currEditorId={} userId={}", itemId, conflictUserId, userId);
            } else {
                logger.warn("delTestItem {}正在编辑该项数据 itemId={}, currEditorId={} userId={}", userMap.getStringAttribute("userName"), itemId, conflictUserId, userId);
                return ApiResponseUtil.error(ErrorCode.E5103, "{}正在编辑，请稍后再试。 itemId={}", userMap.getStringAttribute("userName"), itemId);
            }
        }

        // 然后检查是否被编辑过（不允许同时打开编辑）


        params.remove("itemId");
        params.remove("itemType");
        params.put("deleteFlg", true);
        itemService.setEditStatus(itemId, null, null);
        itemService.saveTestItem(userId, itemId, params);
        updateHisService.saveItemUpdateHis(userObj, "删除测试条件", projId, itemId, params);
        return ApiResponseUtil.success();
    }

    /**
     * 结束编辑状态, 不保存数据！！！
     */
    @ResponseBody
    @RequestMapping(value = "/ajax/endEditable", method = RequestMethod.POST)
    public Map<String, Object> endEditable(@RequestParam Map<String, String> params) {
        Long userId = (Long) request.getSession().getAttribute(Constants.KEY_USER_ID);

        long itemId = StringUtil.convertToLong(params.get("itemId"));
        if (itemId == 0) {
            logger.warn("endEditable 缺少参数itemId");
            return ApiResponseUtil.error(ErrorCode.W1001, "缺少参数itemId");
        }

        BaseMongoMap tblMap = itemService.findTestItem(itemId);
        if (tblMap == null) {
            // 表不存在
            logger.warn("endEditable 数据不存在 itemId={}, userId={}", itemId, userId);
            return ApiResponseUtil.error(ErrorCode.E5101, "指定的数据不存在 itemId={}", itemId);
        }
        Long currEditorId = tblMap.getLongAttribute("currEditorId");
        if (!userId.equals(currEditorId)) {
            logger.warn("endEditable 数据不是由当前用户编辑 itemId={}, currEditorId={} userId={}", itemId, currEditorId, userId);
            BaseMongoMap userMap = userService.getUserByIId(currEditorId);
            if (userMap == null) {
                logger.warn("endEditable 用户不存在 itemId={}, currEditorId={} userId={}", itemId, currEditorId, userId);
            } else {
                logger.warn("endEditable {}正在编辑该项数据 itemId={}, currEditorId={} userId={}", userMap.getStringAttribute("userName"), itemId, currEditorId, userId);
                return ApiResponseUtil.error(ErrorCode.E5101, "{}正在编辑该项数据 itemId={}", userMap.getStringAttribute("userName"), itemId);
            }
        }

        itemService.setEditStatus(itemId, null, null);
        return ApiResponseUtil.success();
    }

}