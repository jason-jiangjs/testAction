package org.vog.testa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.vog.base.model.mongo.BaseMongoMap;
import org.vog.base.service.BaseService;
import org.vog.common.util.DateTimeUtil;
import org.vog.testa.dao.ProjectDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class ProjectService extends BaseService {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ComSequenceService sequenceService;

    /**
     * 查询项目
     */
    public BaseMongoMap findProjectById(long projId) {
        Query queryObj = new Query();
        queryObj.addCriteria(where("_id").is(projId));
        queryObj.addCriteria(where("deleteFlg").is(false));
        return projectDao.getMongoMap(queryObj);
    }

    /**
     * 查询项目
     */
    public BaseMongoMap findProjectByName(String projName) {
        Query queryObj = new Query();
        queryObj.addCriteria(where("projName").is(projName));
        queryObj.addCriteria(where("deleteFlg").is(false));
        return projectDao.getMongoMap(queryObj);
    }

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

    /**
     * 删除项目(逻辑删除)
     */
    public void removeProject(long userId, long projId) {
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("deleteFlg", true);
        infoMap.put("modifier", userId);
        infoMap.put("modifiedTime", DateTimeUtil.getNowTime());
        projectDao.updateObject(projId, infoMap, false);
    }

    /**
     * 保存项目
     */
    public void saveProject(Long projId, Map<String, Object> params) {
        if (projId == null || projId == 0) {
            projId = sequenceService.getNextSequence(ComSequenceService.ComSequenceName.FX_PROJ_ID);
            params.put("_id", projId);
        }
        projectDao.updateObject(projId, params, true);
    }

}
