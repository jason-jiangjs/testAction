package org.vog.testa.dao;

import org.springframework.stereotype.Repository;
import org.vog.base.dao.mongo.BaseMongoDao;

/**
 * 库定义
 */
@Repository
public class ProjectDao extends BaseMongoDao {

    // mongo表名
    private static final String COLL_NAME = "project_list";

    @Override
    public String getTableName() {
        return COLL_NAME;
    }

}
