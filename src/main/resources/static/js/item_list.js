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
        pagination: false
    };
    options.url = Ap_CtxPath + '/ajax/getPageItemList?pageId=' + $('#pageId').val() + '&_t=' + new Date().getTime();
    options.columns = [[
        {field:'group1',title:'分组一',width:100},
        {field:'group2',title:'分组二',width:100},
        {field:'condition1',title:'测试条件1',width:150},
        {field:'condition2',title:'测试条件2',width:50},
        {field:'expectation',title:'期望结果',width:50},
        {field:'testDate',title:'测试日期',width:50},
        {field:'tester',title:'测试者',width:50},
        {field:'result',title:'测试结果',width:50},
        {field:'category',title:'故障分类',width:50},
        {field:'desc',title:'故障描述',width:50},
        {field:'cause',title:'故障原因',width:50}
    ]];


    $('#item_table').datagrid(options);

});
