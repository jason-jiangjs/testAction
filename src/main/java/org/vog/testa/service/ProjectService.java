package org.vog.testa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.vog.base.model.mongo.BaseMongoMap;
import org.vog.base.service.BaseService;
import org.vog.testa.dao.ProjectDao;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class ProjectService extends BaseService {

    @Autowired
    private ProjectDao projectDao;

    /**
     * 查询项目一览
     */
    public List<BaseMongoMap> findProjectList(int page, int limit, boolean checked) {
        Query queryObj = new Query();
        queryObj.fields().include("projName");
        queryObj.fields().include("staTime");
        queryObj.fields().include("endTime");
        queryObj.fields().include("pageCnt");
        queryObj.fields().include("itemCnt");
        queryObj.fields().include("bugCnt");
        if (checked) {
            queryObj.addCriteria(where("deleteFlg").is(false));
        }
        if (limit > 0) {
            queryObj.skip((page - 1) * limit);
            queryObj.limit(limit);
        }
        return projectDao.getMongoMapList(queryObj);
    }

    /**
     * 统计项目个数(全部)
     */
    public long countProjectList() {
        return projectDao.countList(null);
    }

}
