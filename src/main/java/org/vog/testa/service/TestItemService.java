package org.vog.testa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.vog.base.model.mongo.BaseMongoMap;
import org.vog.base.service.BaseService;
import org.vog.testa.dao.TestItemDao;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class TestItemService extends BaseService {

    @Autowired
    private TestItemDao itemDao;

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

}
