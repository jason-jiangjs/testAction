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
                    this.setAttribute('style', 'height:' + (this.scrollHeight) + 'px;width:100%;margin-bottom:-4px;font-size:14px;overflow-y:hidden;');
                });
            },
            resize: function (target, width) {
                $(target)._outerWidth(width);
            }
        }
    });
    $.extend($.fn.datagrid.methods, {
        editCell: function(jq,param){
            return jq.each(function(){
                var opts = $(this).datagrid('options');
                var fields = $(this).datagrid('getColumnFields',true).concat($(this).datagrid('getColumnFields'));
                for(var i=0; i<fields.length; i++){
                    var col = $(this).datagrid('getColumnOption', fields[i]);
                    col.editor1 = col.editor;
                    if (fields[i] != param.field){
                        col.editor = null;
                    }
                }
                $(this).datagrid('beginEdit', param.index);
                for(var i=0; i<fields.length; i++){
                    var col = $(this).datagrid('getColumnOption', fields[i]);
                    col.editor = col.editor1;
                }
            });
        }
    });
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
        {field:'group1',title:'分组一',width:150,editor:'textarea'},
        {field:'group2',title:'分组二',width:150,editor:'textarea'},
        {field:'condition1',title:'测试条件1',width:150,editor:'textarea'},
        {field:'condition2',title:'测试条件2',width:150,editor:'textarea'},
        {field:'expectation',title:'期望结果',width:150,editor:'textarea'},
        {field:'testDate',title:'测试日期',width:100},
        {field:'tester',title:'测试者',width:80},
        {field:'result',title:'测试结果',width:60},
        {field:'category',title:'故障分类',width:150},
        {field:'desc',title:'故障描述',width:150,editor:'textarea'},
        {field:'cause',title:'故障原因',width:150,editor:'textarea'},
        {field:'cfmDate',title:'确认日期',width:100},
        {field:'confirmer',title:'确认者',width:80},
        {field:'cfmResult',title:'确认结果及描述',width:150,editor:'textarea'}
    ]];

    options.onDblClickCell = onClickRowBegEdit;
    $('#item_table').datagrid(options);
});

function onClickRowBegEdit(index, field, value) {

        var gridObj = $('#item_table');
        gridObj.datagrid('selectRow', index).datagrid('editCell', { index: index, field: field });
        var ed = gridObj.datagrid('getEditor', { index: index, field: field });
        if (ed && ed.target) {
            if (field == 'desc') {
                $(ed.target).focus();
            } else {
                if ($(ed.target).closest('td').find("input.textbox-text")[0]) {
                    $(ed.target).closest('td').find("input.textbox-text")[0].focus();
                }
            }
        }

}

function setHeight() {
    var c = $('#cc');
    var p = c.layout('panel','center');    // get the center panel
    var oldHeight = p.panel('panel').outerHeight();
    p.panel('resize', {height:'auto'});
    var newHeight = p.panel('panel').outerHeight();
    c.layout('resize',{
        height: (c.height() + newHeight - oldHeight)
    });
}