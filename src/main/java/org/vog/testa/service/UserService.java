package org.vog.testa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.vog.base.model.mongo.BaseMongoMap;
import org.vog.base.service.BaseService;
import org.vog.testa.dao.UserDao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class UserService extends BaseService {

    @Autowired
    private UserDao userDao;

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
}
