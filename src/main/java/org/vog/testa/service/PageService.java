package org.vog.testa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.vog.base.model.mongo.BaseMongoMap;
import org.vog.base.service.BaseService;
import org.vog.testa.dao.PageDao;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class PageService extends BaseService {

    @Autowired
    private PageDao pageDao;

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

}
