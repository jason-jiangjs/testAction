package org.vog.testa.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.vog.base.controller.BaseController;
import org.vog.base.model.mongo.BaseMongoMap;
import org.vog.common.Constants;
import org.vog.common.ErrorCode;
import org.vog.common.util.AESCoderUtil;
import org.vog.common.util.ApiResponseUtil;
import org.vog.common.util.StringUtil;

import java.util.Map;

/**
 * 用户登录操作
 */
@Controller
public class LoginController extends BaseController {


    /**
     * 登录成功
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public ModelAndView home(@RequestParam Map<String, String> params) {
        ModelAndView model = new ModelAndView();
        int checkFlg = StringUtil.convertToInt(params.get("type"));


            model.setViewName("proj_list");

        return model;
    }

    /**
     * 去修改密码画面
     */
    @RequestMapping(value = "/changePasswd", method = RequestMethod.GET)
    public ModelAndView changePasswd() {
        ModelAndView model = new ModelAndView();
        model.setViewName("changePassword");
        return model;
    }

    /**
     * 保存新密码(session中必须已有user_id)
     */
    @ResponseBody
    @RequestMapping(value = "/ajax/savePasswd", method = RequestMethod.POST)
    public Map<String, Object> savePasswd(@RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getSession().getAttribute(Constants.KEY_USER_ID);

        String oldPasswd = StringUtils.trimToNull((String) params.get("oldPasswd"));
        String newPasswd = StringUtils.trimToNull((String) params.get("newPasswd"));
        String newPasswdCfm = StringUtils.trimToNull((String) params.get("newPasswdCfm"));
//        if (oldPasswd == null || newPasswd == null || newPasswdCfm == null) {
//            logger.warn("savePasswd 缺少参数 params={}", params.toString());
//            return ApiResponseUtil.error(ErrorCode.W1001, "缺少必须值.");
//        }
//
//        BaseMongoMap userObj = userService.getUserById(userId);
//
//        // 先密码匹配验证
//        BCryptPasswordEncoder cryptEncoder = new BCryptPasswordEncoder();
//        if (!cryptEncoder.matches(oldPasswd, userObj.getStringAttribute("password"))) {
//            logger.warn("savePasswd 旧密码错误");
//            return ApiResponseUtil.error(ErrorCode.E5010, "旧密码错误.");
//        }
//
//        userService.savePassword(userId, cryptEncoder.encode(newPasswd));
        return ApiResponseUtil.success();
    }

    /**
     * 设置/取消缺省工作环境
     */
    @ResponseBody
    @RequestMapping(value = "/ajax/setDefaultDbEnv", method = RequestMethod.POST)
    public Map<String, Object> setDefaultDbEnv(@RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getSession().getAttribute(Constants.KEY_USER_ID);
//        CustomerUserDetails userObj = (CustomerUserDetails) ((Authentication) request.getUserPrincipal()).getPrincipal();
//
//        int checkFlg = StringUtil.convertToInt(params.get("checkFlg"));
//        if (checkFlg == 1) {
//            // 保存默认工作环境
//            long dbId = StringUtil.convertToLong(params.get("dbId"));
//            if (dbId == 0) {
//                logger.warn("getColumnList 缺少dbId userId={}", userId);
//                return ApiResponseUtil.error(ErrorCode.W1001, "错误操作,未选择指定的表.(缺少参数 dbId)");
//            }
//            userService.setUserFavorite(userObj.getId(), dbId);
//        } else {
//            // 取消默认工作环境
//            userService.setUserFavorite(userObj.getId(), 0);
//        }

        return ApiResponseUtil.success();
    }
}
