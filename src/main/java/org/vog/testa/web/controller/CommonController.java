package org.vog.testa.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.vog.base.controller.BaseController;
import org.vog.base.model.mongo.BaseMongoMap;
import org.vog.testa.service.PageService;

import java.util.List;
import java.util.Map;

/**
 * 查询表及列的一览
 */
@Controller
public class CommonController extends BaseController {

    @Autowired
    private PageService pageService;

    /**
     * 转到登录画面
     * TODO-- 期待更好方案，目前是因为AuthenticationFailureHandlerImpl要传参数到页面
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ModelAndView tologin(@RequestParam Map<String, String> params) {
        // 计算画面数、故障数等等
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // 先统计各个画面的
//                int idx = 1;
//                while (true) {
//                    List<BaseMongoMap> pageList = pageService.findPageList(idx, 500, null);
//                    if (pageList.isEmpty()) {
//                        break;
//                    }
//                    for (BaseMongoMap pageMap : pageList) {
//
//                    }
//                }
//
//
//            }
//        }).start();

        ModelAndView model = new ModelAndView();
        model.setViewName("index");
        model.addObject("errMsg", params.get("msg"));
        return model;
    }

}
//    List<WriteModel<Document>> requests = new ArrayList<WriteModel<Document>>();
//    for (Document document : documents) {
//            //更新条件
//            Document queryDocument = new Document("_id",document.get("_id"));
//            //更新内容，改下书的价格
//            Document updateDocument = new Document("$set",new Document("price","30.6"));
//            //构造更新单个文档的操作模型
//            UpdateOneModel<Document> uom = new UpdateOneModel<Document>(queryDocument,updateDocument,new UpdateOptions().upsert(false));
//        //UpdateOptions代表批量更新操作未匹配到查询条件时的动作，默认false，什么都不干，true时表示将一个新的Document插入数据库，他是查询部分和更新部分的结合
//        requests.add(uom);
//        }
//        BulkWriteResult bulkWriteResult = collection.bulkWrite(requests);
//        System.out.println(bulkWriteResult.toString());