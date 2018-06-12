/**
 * 数据库设计管理
 */

// 画面项目初始化，加载数据库一览
$(function () {

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
    options.onDblClickCell = onCheckBegEdit;

    options.url = Ap_CtxPath + '/ajax/getPageItemList?pageId=' + $('#pageId').val() + '&_t=' + new Date().getTime();
    options.columns = [[
        {field:'group1',title:'分组一',width:150,formatter:descformatter},
        {field:'group2',title:'分组二',width:150,formatter:descformatter},
        {field:'condition1',title:'测试条件1',width:150,formatter:descformatter},
        {field:'condition2',title:'测试条件2',width:150,formatter:descformatter},
        {field:'expectation',title:'期望结果',width:150,formatter:descformatter},
        {field:'testDate',title:'测试日期',width:100},
        {field:'tester',title:'测试者',width:80},
        {field:'result',title:'结果',width:50},
        {field:'category',title:'故障分类',width:150},
        {field:'desc',title:'故障描述',width:150,formatter:descformatter},
        {field:'cause',title:'故障原因',width:150,formatter:descformatter},
        {field:'cfmDate',title:'确认日期',width:100},
        {field:'confirmer',title:'确认者',width:80},
        {field:'cfmResult',title:'确认结果及描述',width:150,formatter:descformatter}
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
    editable = $.trim($('#editable').val());
});


// 是否可编辑
var editable = 0;
// 当前行的MD5值
var _currRowIdxMD5 = null;

// 行切换时的处理，保存数据、检查是否可编辑等
// 如果没有编辑权限，则不做处理
// 最终方案，不在行内直接编辑，使用弹出框口，技术上实现更简单，虽然使用上稍有不便
// 双击不同列，弹出不同窗口
function onCheckBegEdit(index, field, value) {
    console.log(index + ' ' + field + ' ' + value);
    if (editable == '' || editable == '0') {
        return;
    }

    var gridObj = $('#item_table');
    var s1 = gridObj.datagrid('getSelected');
    if (s1 == null || s1 == undefined) {
        layer.msg('请选择后再操作．')
        return;
    }
    var s2 = gridObj.datagrid('getRowIndex', s1._id);
    if (s2 < 0) {
        layer.msg('数据错误，请刷新画面后再操作．')
        return;
    }

    editable = false;
    var errMsg = null;
    var loadLy = layer.load(1);
    var last_upd = $.trim(s1.modifiedTime);
    $.ajax({
        type: 'post',
        async: false,
        url: Ap_CtxPath + '/ajax/chkItemEditable?itemId=' + s1._id + '&lastUpd=' + last_upd,
        success: function (data) {
            layer.close(loadLy);
            if (data.code == 0) {
                // 可以开始编辑
                editable = true;
            } else if (data.code == 1) {
                // 有人正在编辑
                errMsg = data.msg;
            } else if (data.code == 2) {
                // 已经被修改过了

            } else {
                errMsg = data.msg + ' code=' + data.code;
            }
        }
    });

    if (editable == false) {
        // 不可编辑
        layer.msg(errMsg);
        return false;
    }

    // _currRowIdxMD5 = $.md5($.trim(oldObj.group1) + '||' + $.trim(oldObj.group2) + '||' + $.trim(oldObj.condition1) + '||' + $.trim(oldObj.condition2) + '||' + $.trim(oldObj.expectation)
    //     + '||' + $.trim(oldObj.result) + '||' + $.trim(oldObj.category) + '||' + $.trim(oldObj.desc) + '||' + $.trim(oldObj.cause) + '||' + $.trim(oldObj.cfmResult));

    // 双击不同列，弹出不同窗口
    if (field == 'group1' || field == 'group2' || field == 'condition1' || field == 'condition2' || field == 'expectation') {
        $('#editDlg1').dialog({
            title: '测试条件',
            onBeforeClose: function() {
                return _cfmEndEdit(s1._id, '#editDlg1');
            }
        });
        $('#itemId').val(s1._id);
        $('#group1').textbox('setValue', s1.group1);
        $('#group2').textbox('setValue', s1.group2);
        $('#condition1').textbox('setValue', s1.condition1);
        $('#condition2').textbox('setValue', s1.condition2);
        $('#expectation').textbox('setValue', s1.expectation);
        $('#editDlg1').dialog('open');
        return;
    }

    if (field == 'testDate' || field == 'tester' || field == 'result' || field == 'category' || field == 'desc' || field == 'cause') {
        if ($.trim(s1.group1) + $.trim(s1.group2) + $.trim(s1.condition1) + $.trim(s1.condition2) + $.trim(s1.expectation) == '') {
            return;
        }

        $('#editDlg2').dialog({
            title: '测试结果',
            onBeforeClose: function() {
                return _cfmEndEdit(s1._id, '#editDlg2');
            }
        });
        $('#itemId').val(s1._id);
        $('#testDate').textbox('setValue', s1.testDate);
        $('#tester').textbox('setValue', s1.tester);
        $('#result').textbox('setValue', s1.result);
        $('#category').textbox('setValue', s1.category);
        $('#desc').textbox('setValue', s1.desc);
        $('#cause').textbox('setValue', s1.cause);
        $('#editDlg2').dialog('open');
        return;
    }

    if (field == 'cfmDate' || field == 'confirmer' || field == 'cfmResult') {
        if ($.trim(s1.result) + $.trim(s1.category) + $.trim(s1.desc) + $.trim(s1.cause) == '') {
            return;
        }

        $('#editDlg3').dialog({
            title: '结果确认',
            onBeforeClose: function() {
                return _cfmEndEdit(s1._id, '#editDlg3');
            }
        });
        $('#itemId').val(s1._id);
        $('#cfmDate').textbox('setValue', s1.cfmDate);
        $('#confirmer').textbox('setValue', s1.confirmer);
        $('#cfmResult').textbox('setValue', s1.cfmResult);
        $('#editDlg3').dialog('open');
        return;
    }
    return true;
}

// 检查数据的MD5值，这里不判断参数为空的情况，由调用时处理
function _checkItemMD5(oldObj, newObj) {
    var oldMD5 = $.md5($.trim(oldObj.group1) + '||' + $.trim(oldObj.group2) + '||' + $.trim(oldObj.condition1) + '||' + $.trim(oldObj.condition2) + '||' + $.trim(oldObj.expectation)
        + '||' + $.trim(oldObj.result) + '||' + $.trim(oldObj.category) + '||' + $.trim(oldObj.desc) + '||' + $.trim(oldObj.cause) + '||' + $.trim(oldObj.cfmResult));

    var newMD5 = $.md5($.trim(newObj.group1) + '||' + $.trim(newObj.group2) + '||' + $.trim(newObj.condition1) + '||' + $.trim(newObj.condition2) + '||' + $.trim(newObj.expectation)
        + '||' + $.trim(newObj.result) + '||' + $.trim(newObj.category) + '||' + $.trim(newObj.desc) + '||' + $.trim(newObj.cause) + '||' + $.trim(newObj.cfmResult));

    return oldMD5 == newMD5;
}


// 确认是否结束编辑
function _cfmEndEdit(itemId, dlgId) {


    var rst = confirm('确定要结束编辑?\n不保存当前修改内容，该操作不可恢复，是否确认结束?');
    if (rst == true) {
        // 提交到后台，取消编辑状态
        // $(dlgId).dialog('close');

        return true;
    } else {
        return false;
    }

}

// 备注一栏的显示形式
function descformatter(value, row, index) {
    if (value) {
        var reg = new RegExp("\n", "g");
        var str = value.replace(reg, "<br/>");
        return '<div style="width:100%;display:block;word-break: break-all;word-wrap: break-word;margin-top:5px;margin-bottom:5px">' + str + '</div>';
    }
    return '';
}

// 添加测试项
function newItem() {
    // 先取得当前选择行
    var gridObj = $('#page_grid');
    var s1 = gridObj.datagrid('getSelected');
    if (s1 == null || s1 == undefined) {
        layer.msg('请选择一个数据库后再操作．')
        return;
    }
    var s2 = gridObj.datagrid('getRowIndex', s1._id);
    if (s2 < 0) {
        layer.msg('数据错误，请刷新画面后再操作．')
        return;
    }

    $('#editDlg').dialog({
        title: '编辑 - ' + s1.pageName
    });

    $('#pageId').val(s1._id);
    $('#group1').textbox('setValue', s1.group1);
    $('#group2').textbox('setValue', s1.group2);
    $('#pageName').textbox('setValue', s1.pageName);
    $('#diffiLevel').numberbox('setValue', s1.diffiLevel);
    $('#codeCnt').numberbox('setValue', s1.codeCnt);
    $('#remarks').textbox('setValue', s1.desc);
    $('#editDlg').dialog('open');
}

// 插入测试项，必须先检查是否有其他人正在编辑，若有，则不允许操作
function insertItem() {
    // 先在grid里加一行，然后弹出对话框
    $('#editDlg').dialog({
        title: '新增'
    });

    $('#pageId').val(null);
    $('#group1').textbox('setValue', null);
    $('#group2').textbox('setValue', null);
    $('#pageName').textbox('setValue', null);
    $('#diffiLevel').numberbox('setValue', null);
    $('#codeCnt').numberbox('setValue', null);
    $('#remarks').textbox('setValue', null);
    $('#editDlg').dialog('open');
}

// 删除测试项，必须先检查是否有其他人正在编辑，若有，则不允许操作
function delItem() {

}

// 批量导入，必须先检查是否有其他人正在编辑，若有，则不允许操作
// 已有数据时暂不支持批量导入
function importItem() {

}

// 提交修改(保存)
function submitForm1() {
    // 不判断是否已修改，全部提交至后台
    var postData = {};
    postData.itemId = $.trim($('#itemId').val());
    postData.group1 = $.trim($('#group1').textbox('getValue'));
    postData.group2 = $.trim($('#group2').textbox('getValue'));
    postData.condition1 = $.trim($('#condition1').textbox('getValue'));
    postData.condition2 = $.trim($('#condition2').textbox('getValue'));
    postData.expectation = $.trim($('#expectation').textbox('getValue'));

    // 先验证必须值
    if (postData.expectation == '' || (postData.condition1 == '' && postData.condition2 == '')) {
        layer.msg("必须输入测试条件和期望结果！");
        return;
    }

    var loadLy = layer.load(1);
    $.ajax({
        type: 'post',
        url: Ap_CtxPath + '/ajax/saveCondition',
        data: JSON.stringify(postData),
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            layer.close(loadLy);
            if (data.code == 0) {
                setTimeout("_endSave('#editDlg1')", 1000);
                layer.msg('保存成功，将关闭对话框．', {time: 1000});
            } else {
                layer.msg(data.msg + ' code=' + data.code);
            }
        }
    });
}

// 提交修改(保存)
function submitForm2() {
    // 不判断是否已修改，全部提交至后台
    var postData = {};
    postData.itemId = $.trim($('#itemId').val());
    postData.result = $.trim($('#result').textbox('getValue'));
    postData.category = $.trim($('#category').textbox('getValue'));
    postData.desc = $.trim($('#desc').textbox('getValue'));
    postData.cause = $.trim($('#cause').textbox('getValue'));

    // 先验证必须值
    if (postData.result == '' || postData.category == '' || postData.desc == '') {
        layer.msg("必须输入故障现象！");
        return;
    }

    var loadLy = layer.load(1);
    $.ajax({
        type: 'post',
        url: Ap_CtxPath + '/ajax/saveTestRsult',
        data: JSON.stringify(postData),
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            layer.close(loadLy);
            if (data.code == 0) {
                layer.msg('保存成功，将关闭对话框．', {time: 1000});
                setTimeout("_endSave('#editDlg2')", 1000);
            } else {
                layer.msg(data.msg + ' code=' + data.code);
            }
        }
    });
}

// 提交修改(保存)
function submitForm3() {
    // 不判断是否已修改，全部提交至后台
    var postData = {};
    postData.itemId = $.trim($('#itemId').val());
    postData.cfmResult = $.trim($('#cfmResult').textbox('getValue'));

    // 先验证必须值
    if (postData.cfmResult == '') {
        layer.msg("必须输入确认结果！");
        return;
    }

    var loadLy = layer.load(1);
    $.ajax({
        type: 'post',
        url: Ap_CtxPath + '/ajax/saveConfirmRsult',
        data: JSON.stringify(postData),
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            layer.close(loadLy);
            if (data.code == 0) {
                layer.msg('保存成功，将关闭对话框．');
                setTimeout("_endSave('#editDlg3')", 1000);
            } else {
                layer.msg(data.msg + ' code=' + data.code);
            }
        }
    });
}

// 关闭对话框,刷新一览(当前分页)
function _endSave(dlgId) {
    $('#item_table').datagrid('reload', {});
    $(dlgId).dialog('close', true);
}
