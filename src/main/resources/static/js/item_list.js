/**
 * 数据库设计管理
 */

// 画面项目初始化，加载数据库一览
$(function () {

    $.extend($.fn.datagrid.defaults.editors, {
        textarea: {
            init: function (container, options) {
                var input = $('<textarea >').appendTo(container);
                return input;
            },
            destroy: function (target) {
                $(target).remove();
            },
            getValue: function (target) {
                return $(target).val();
            },
            setValue: function (target, value) {
                $(target).val(value);
                // 编辑时，加载内容后，重新设置textarea高度
                $(target).each(function () {
                    this.setAttribute('style', 'min-height:60px;margin-bottom:-4px;font-size:14px;overflow-y:hidden;');
                });
            },
            resize: function (target, width) {
                $(target)._outerWidth(width);
            }
        }
    });

    var testRstDef = [{"value":"OK","text":"OK"},{"value":"NG","text":"NG"},{"value":"WT","text":"WT"}];

    // 加载列定义
    var options = {
        idField: "_id",
        rownumbers: true,
        fit: false,

        nowrap: false,
        striped: true,
        singleSelect: true,
        method: 'get',
        pagination: false
    };
    options.url = Ap_CtxPath + '/ajax/getPageItemList?pageId=' + $('#pageId').val() + '&_t=' + new Date().getTime();
    options.columns = [[
        {field:'group1',title:'分组一',width:150,editor:'textarea',formatter:descformatter},
        {field:'group2',title:'分组二',width:150,editor:'textarea',formatter:descformatter},
        {field:'condition1',title:'测试条件1',width:150,editor:'textarea',formatter:descformatter},
        {field:'condition2',title:'测试条件2',width:150,editor:'textarea',formatter:descformatter},
        {field:'expectation',title:'期望结果',width:150,editor:'textarea',formatter:descformatter},
        {field:'testDate',title:'测试日期',width:100},
        {field:'tester',title:'测试者',width:80},
        {field:'result',title:'结果',width:50, editor:{
                type: 'combobox',
                options: {
                    data: testRstDef,
                    valueField: "value",
                    textField: "text",
                    editable: false,
                    panelHeight: 100,
                    required: true
                }
            }},
        {field:'category',title:'故障分类',width:150},
        {field:'desc',title:'故障描述',width:150,editor:'textarea',formatter:descformatter},
        {field:'cause',title:'故障原因',width:150,editor:'textarea',formatter:descformatter},
        {field:'cfmDate',title:'确认日期',width:100},
        {field:'confirmer',title:'确认者',width:80},
        {field:'cfmResult',title:'确认结果及描述',width:150,editor:'textarea',formatter:descformatter}
    ]];
    $('#item_table').datagrid(options);
    $('#item_table').datagrid('resize', {
        height: $(window).height() - 50
    });
    $(window).resize(function() {
        $('#item_table').datagrid('resize', {
            width: $(window).width(),
            height: $(window).height() - 50
        });
    });
});

// 备注一栏的显示形式
function descformatter(value, row, index) {
    if (value) {
        var reg = new RegExp("\n", "g");
        var str = value.replace(reg, "<br/>");
        var dspDiv = '';
        if (str.indexOf("<br/>") >= 0) {
            dspDiv = '<div style="width:100%;display:block;word-break: break-all;word-wrap: break-word;margin-top:4px;margin-bottom: 4px">' + str + '</div>';
        } else {
            dspDiv = '<div style="width:100%;display:block;word-break: break-all;word-wrap: break-word">' + str + '</div>';
        }
        return dspDiv;
    }
    return '';
}

// 开始编辑
function enableEdit() {
    $('#item_table').datagrid('enableCellEditing');
}

// 取消编辑
function cancelEdit() {
    $('#item_table').datagrid('disableCellEditing');
    $('#item_table').datagrid('disableCellSelecting');
    $('#item_table').datagrid('gotoCell', {
        index: 0,
        field: 'testDate'
    });
}

// 保存编辑
function saveEdit() {

}
