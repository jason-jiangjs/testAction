/**
 * 数据库设计管理
 */

// 画面项目初始化，加载数据库一览
$(function () {

    // 加载列定义
    var options = {
        idField: "_id",
        lines: true,
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
    options.url = Ap_CtxPath + '/ajax/getProjPageList?projId=' + $('#projId').val() + '&_t=' + new Date().getTime();
    options.columns = [[
        {field:'group1',title:'分组一',width:100},
        {field:'group2',title:'分组二',width:100},
        {field:'pageName',title:'画面名称',width:150},
        {field:'itemCnt',title:'项目数',width:50},
        {field:'dnfCnt',title:'未完成数',width:50},
        {field:'bugCnt',title:'故障数',width:50}
    ]];

    options.onDblClickRow = function(index, row) {
        window.location.href = Ap_CtxPath + '/item_list?pageId=' + row._id;
    };

    $('#page_grid').datagrid(options);

});
