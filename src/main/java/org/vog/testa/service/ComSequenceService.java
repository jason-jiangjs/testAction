package org.vog.testa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vog.testa.dao.ComSequenceDao;

@Service
public class ComSequenceService {

    /**
     * Sequence Name
     */
    public enum ComSequenceName {
        FX_PROJ_ID("FX_PROJ_ID"),
        FX_PAGE_ID("FX_PAGE_ID"),
        FX_ITEM_ID("FX_ITEM_ID"),
        FX_USER_ID("FX_USER_ID"),
        ;

        // 成员变量
        private String name;

        // 构造方法
        private ComSequenceName(String name) {
            this.name = name;
        }

        // get set 方法
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Autowired
    private ComSequenceDao sequenceDao;

    /**
     * 取得 下一个 Sequence
     */
    public long getNextSequence(ComSequenceName nameEnum) {
        return sequenceDao.getNextSequence(nameEnum.getName());
    }

}
