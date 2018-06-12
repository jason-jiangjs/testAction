package org.vog.testa.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vog.base.model.mongo.BaseMongoMap;
import org.vog.base.service.BaseService;
import org.vog.common.util.DateTimeUtil;
import org.vog.common.util.JacksonUtil;
import org.vog.testa.dao.UpdateHisDao;
import org.vog.testa.web.login.CustomerUserDetails;

import java.util.Map;

@Service
public class UpdateHisService extends BaseService {

    @Autowired
    private UpdateHisDao updateHisDao;

    /**
     * 保存操作历史
     */
    public void saveUpdateHis(CustomerUserDetails userObj, long projId, BaseMongoMap infoMap, Map<String, Object> params) {
        DBObject data = new BasicDBObject();
        data.put("projId", projId);
        int hisType = 0;
        if (infoMap == null) {
            // 新增
            hisType = 1;
            data.put("tableId", params.get("_tbl_id"));
            data.put("tableName", params.get("_tbl_name"));
            data.put("contentAft", JacksonUtil.bean2Json(params));
        } else if (params == null) {
            // 删除
            hisType = 3;
            data.put("tableId", infoMap.getLongAttribute("_id"));
            data.put("tableName", infoMap.getStringAttribute("tableName"));
            data.put("contentBef", infoMap.toString());
        } else {
            // 修改
            hisType = 2;
            data.put("tableId", infoMap.getLongAttribute("_id"));
            data.put("tableName", infoMap.getStringAttribute("tableName"));
            data.put("contentBef", infoMap.toString());
            data.put("contentAft", JacksonUtil.bean2Json(params));
        }

        data.put("type", hisType);
        data.put("userId", userObj.getId());
        data.put("userName", userObj.getUsername());
        data.put("modifiedTime", DateTimeUtil.getNowTime());
        updateHisDao.insertObject(data);
    }

    /**
     * 保存操作历史
     */
    public void saveItemUpdateHis(CustomerUserDetails userObj, String hisType, long projId, long itemId, Map<String, Object> params) {
        DBObject data = new BasicDBObject();
        data.put("projId", projId);
        data.put("itemId", itemId);
        data.put("contentAft", JacksonUtil.bean2Json(params));
        data.put("type", hisType);
        data.put("userId", userObj.getId());
        data.put("userName", userObj.getUsername());
        data.put("modifiedTime", DateTimeUtil.getNowTime());
        updateHisDao.insertObject(data);
    }

}
