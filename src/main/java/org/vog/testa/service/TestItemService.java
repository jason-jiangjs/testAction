package org.vog.testa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.vog.base.model.mongo.BaseMongoMap;
import org.vog.base.service.BaseService;
import org.vog.common.util.DateTimeUtil;
import org.vog.testa.dao.TestItemDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class TestItemService extends BaseService {

    @Autowired
    private TestItemDao itemDao;

    @Autowired
    private ComSequenceService sequenceService;

    /**
     * 查询指定测试项目
     */
    public BaseMongoMap findTestItem(long itemId) {
        Query queryObj = new Query();
        queryObj.addCriteria(where("_id").is(itemId));
        queryObj.addCriteria(where("deleteFlg").is(false));

        return itemDao.getMongoMap(queryObj);
    }

    /**
     * 查询项目一览(不分页)
     */
    public List<BaseMongoMap> findTestItemList(long pageId) {
        Query queryObj = new Query();
        queryObj.addCriteria(where("pageId").is(pageId));
        queryObj.addCriteria(where("deleteFlg").is(false));

        return itemDao.getMongoMapList(queryObj);
    }

    /**
     * 统计项目个数(全部)
     */
    public long countTestItemList(long pageId) {
        Query queryObj = new Query();
        queryObj.addCriteria(where("pageId").is(pageId));
        queryObj.addCriteria(where("deleteFlg").is(false));
        return itemDao.countList(queryObj);
    }

    /**
     * 开始编辑表
     */
    public void setEditStatus(long itemId, Long userId, Long updTime) {
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("currEditorId", userId);
        infoMap.put("startEditTime", updTime);

        itemDao.updateObject(itemId, infoMap, false);
    }

    /**
     * 保存项目
     */
    public void saveTestItem(Long userId, Long itemId, Map<String, Object> params) {
        if (itemId == null || itemId == 0) {
            itemId = sequenceService.getNextSequence(ComSequenceService.ComSequenceName.FX_ITEM_ID);
            params.put("_id", itemId);
            params.put("creator", userId);
            params.put("createdTime", DateTimeUtil.getNowTime());
        } else {
            params.put("modifier", userId);
            params.put("modifiedTime", DateTimeUtil.getNowTime());
        }
        itemDao.updateObject(itemId, params, true);
    }
}
