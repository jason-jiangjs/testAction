/**
 * 数据库设计管理
 */

// 画面项目初始化，加载数据库一览
$(function () {

    // 加载列定义
    var options = {
        idField: "_id",
        fit: true,
        fitColumns: true,
        nowrap: false,
        striped: true,
        singleSelect: true,
        method: 'get',
        pagination: true,
        pageSize: 20,
        pageList: [20,50,100]
    };
    options.url = Ap_CtxPath + '/ajax/getProjList?_t=' + new Date().getTime();
    options.columns = [[
        {field:'projName',title:'项目名称',width:280},
        {field:'staTime',title:'开始时间',width:50},
        {field:'endTime',title:'结束时间',width:50},
        {field:'pageCnt',title:'画面数',width:50},
        {field:'itemCnt',title:'项目数',width:50},
        {field:'bugCnt',title:'故障数',width:50}
    ]];

    options.onDblClickRow = function(index, row) {
        window.location.href = Ap_CtxPath + "/page_list?projId=" + row._id;
    };

    $('#proj_grid').datagrid(options);

});
