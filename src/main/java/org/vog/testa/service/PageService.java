package org.vog.testa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.vog.base.model.mongo.BaseMongoMap;
import org.vog.base.service.BaseService;
import org.vog.common.util.DateTimeUtil;
import org.vog.testa.dao.PageDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class PageService extends BaseService {

    @Autowired
    private PageDao pageDao;

    @Autowired
    private ComSequenceService sequenceService;

    /**
     * 查询项目
     */
    public BaseMongoMap findPageById(long pageId) {
        Query queryObj = new Query();
        queryObj.addCriteria(where("_id").is(pageId));
        queryObj.addCriteria(where("deleteFlg").is(false));
        return pageDao.getMongoMap(queryObj);
    }

    /**
     * 查询项目
     */
    public BaseMongoMap findPageByName(long projId, String pageName) {
        Query queryObj = new Query();
        queryObj.addCriteria(where("projId").is(projId));
        queryObj.addCriteria(where("pageName").is(pageName));
        queryObj.addCriteria(where("deleteFlg").is(false));
        return pageDao.getMongoMap(queryObj);
    }

    /**
     * 查询项目一览
     */
    public List<BaseMongoMap> findPageList(int page, int limit, long projId) {
        Query queryObj = new Query();
        queryObj.addCriteria(where("projId").is(projId));
        queryObj.addCriteria(where("deleteFlg").is(false));

        if (limit > 0) {
            queryObj.skip((page - 1) * limit);
            queryObj.limit(limit);
        }
        return pageDao.getMongoMapList(queryObj);
    }

    /**
     * 统计项目个数(全部)
     */
    public long countPageList(long projId) {
        Query queryObj = new Query();
        queryObj.addCriteria(where("projId").is(projId));
        queryObj.addCriteria(where("deleteFlg").is(false));
        return pageDao.countList(queryObj);
    }

    /**
     * 删除画面(逻辑删除)
     */
    public void removePage(long userId, long pageId) {
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("deleteFlg", true);
        infoMap.put("modifier", userId);
        infoMap.put("modifiedTime", DateTimeUtil.getNowTime());
        pageDao.updateObject(pageId, infoMap, false);
    }

    /**
     * 保存画面
     */
    public void savePage(Long pageId, Map<String, Object> params) {
        if (pageId == null || pageId == 0) {
            pageId = sequenceService.getNextSequence(ComSequenceService.ComSequenceName.FX_PAGE_ID);
            params.put("_id", pageId);
        }
        pageDao.updateObject(pageId, params, true);
    }

}
