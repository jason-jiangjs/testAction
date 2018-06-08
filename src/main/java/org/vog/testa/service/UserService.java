package org.vog.testa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.vog.base.model.mongo.BaseMongoMap;
import org.vog.base.service.BaseService;
import org.vog.common.util.AESCoderUtil;
import org.vog.common.util.StringUtil;
import org.vog.testa.dao.ProjectDao;
import org.vog.testa.dao.UserDao;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class UserService extends BaseService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProjectService projectService;

    /**
     * 根据id查询用户
     */
    public BaseMongoMap getUserByIId(long iid) {
        Query queryObj = new Query(where("_id").is(iid));
        queryObj.addCriteria(where("deleteFlg").is(false));
        return userDao.getMongoMap(queryObj);
    }

    /**
     * 根据登录帐号（已注册的）查询用户
     */
    public BaseMongoMap getUserByAccount(String userId) {
        Query queryObj = new Query(where("userId").is(userId));
        queryObj.addCriteria(where("deleteFlg").is(false));

        BaseMongoMap employee = userDao.getMongoMap(queryObj);
        return employee;
    }

    /**
     * 查询用户权限信息
     */
    public List<Map<String, Object>> findUserRoleList(long userId) {
        Query queryObj = new Query(where("_id").is(userId));
        queryObj.addCriteria(where("deleteFlg").is(false));
        queryObj.fields().include("role");
        queryObj.fields().include("roleList");
        BaseMongoMap userMap = userDao.getMongoMap(queryObj);
        if (userMap == null) {
            return Collections.EMPTY_LIST;
        }

        List<Map<String, Object>> roleList = null;
        // 如果该用户是系统管理员，则返回所有的数据库一览
        if (userMap.getIntAttribute("role") == 9) {
            List<BaseMongoMap> projList = projectService.findProjectList(0, 0, true);
            if (projList == null || projList.isEmpty()) {
                logger.error("getUserRoleList 无项目数据 iid={}", userId);
                return Collections.EMPTY_LIST;
            }
            roleList = new ArrayList<>();
            for (BaseMongoMap projMap : projList) {
                Map<String, Object> item = new HashMap<>();
                item.put("projId", projMap.getLongAttribute("_id"));
                item.put("projName", projMap.getStringAttribute("projName"));
                item.put("role", 9);
                roleList.add(item);
            }
            return roleList;
        }

        // 如果不是，只返回当前用户有权限操作的数据库一览
        roleList = (List<Map<String, Object>>) userMap.get("roleList");
        if (roleList == null || roleList.isEmpty()) {
            logger.warn("getUserRoleList 该用户未设置权限 iid={}", userId);
            return Collections.EMPTY_LIST;
        }
        Iterator<Map<String, Object>> roleIter = roleList.iterator();
        while (roleIter.hasNext()) {
            Map<String, Object> roleMap = roleIter.next();
            BaseMongoMap projMap = projectService.findProjectById(StringUtil.convertToLong(roleMap.get("projId")));
            if (projMap == null) {
                roleIter.remove();
                continue;
            }
            roleMap.put("projName", projMap.getStringAttribute("projName"));
        }
        return roleList;
    }

    /**
     * 查询用户权限信息
     */
    public boolean checkUserRole(long userId, long projId) {
        List<Map<String, Object>> roleList = findUserRoleList(userId);
        if (roleList.isEmpty()) {
            return false;
        }
        Iterator<Map<String, Object>> roleIter = roleList.iterator();
        while (roleIter.hasNext()) {
            Map<String, Object> roleMap = roleIter.next();
            if (projId == StringUtil.convertToLong(roleMap.get("projId"))) {
                return true;
            }
        }
        return false;
    }
}
