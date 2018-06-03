/**
 * 数据库设计管理
 */

// 画面项目初始化，加载数据库一览
$(function () {

    // 加载列定义
    var options = {
        idField: "_id",
        fit: false,
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
        {field:'itemCnt',title:'测试项目总数',width:50},
        {field:'bugCnt',title:'故障数',width:50}
    ]];

    options.onDblClickRow = function(index, row) {
        window.location.href = Ap_CtxPath + "/page_list?projId=" + row._id;
    };

    $('#proj_grid').datagrid(options);
    $('#proj_grid').datagrid('resize', {
        height: $(window).height() - 75
    });
    $(window).resize(function() {
        $('#proj_grid').datagrid('resize', {
            width: $(window).width(),
            height: $(window).height() - 75
        });
    });

});

// 编辑
function editProject() {
    // 先取得当前选择行
    var gridObj = $('#proj_grid');
    var s1 = gridObj.datagrid('getSelected');
    if (s1 == null || s1 == undefined) {
        layer.msg('请选择一项后再操作．')
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
    $('#delBtn').show();
    $('#projId').val(s1._id);
    $('#projName').textbox('setValue', s1.projName);
    $('#begTime').numberbox('setValue', s1.staTime);
    $('#endTime').numberbox('setValue', s1.endTime);
    $('#remarks').textbox('setValue', s1.desc);
    $('#editDlg').dialog('open');
}

// 新增
function newProject() {
    $('#editDlg').dialog({
        title: '新增'
    });
    $('#delBtn').hide();
    $('#projId').val(null);
    $('#projName').textbox('setValue', null);
    $('#begTime').numberbox('setValue', null);
    $('#endTime').numberbox('setValue', null);
    $('#remarks').textbox('setValue', null);
    $('#editDlg').dialog('open');
}

// 提交修改(保存)
function submitForm() {
    // 不判断是否已修改，全部提交至后台
    var postData = {};
    postData.projId = $('#projId').val();
    postData.pageId = $('#pageId').val();
    postData.group1 = $.trim($('#group1').textbox('getValue'));
    postData.group2 = $.trim($('#group2').textbox('getValue'));
    postData.pageName = $.trim($('#pageName').textbox('getValue'));
    postData.diffiLevel = $.trim($('#diffiLevel').textbox('getValue'));
    postData.remarks = $.trim($('#remarks').textbox('getValue'));

    // 先验证必须值
    if (postData.pageName == '') {
        layer.msg("必须输入画面名称！");
        return;
    }

    var loadLy = layer.load(1);
    $.ajax({
        type: 'post',
        url: Ap_CtxPath + '/ajax/mng/savePageInfo',
        data: JSON.stringify(postData),
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            layer.close(loadLy);
            if (data.code == 0) {
                layer.msg('保存成功，将关闭对话框．');
                setTimeout("_endSave()", 1000)
            } else {
                layer.msg(data.msg + ' code=' + data.code);
            }
        }
    });
}


// 删除画面
function deleteProject() {
    var pageName = $.trim($('#pageName').textbox('getValue'));
    var postData = {};
    postData.projId = $('#projId').val();
    postData.pageId = $('#pageId').val();

    layer.confirm('确定要删除选定的画面［' + pageName + '］?<br>该操作不可恢复，是否确认删除?', { icon: 7,
        btn: ['确定','取消'] //按钮
    }, function(index) {
        // 提交请求到后台
        var loadLy = layer.load(1);
        $.ajax({
            type: 'post',
            url: Ap_CtxPath + '/ajax/mng/delPage',
            data: JSON.stringify(postData),
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                layer.close(loadLy);
                if (data.code == 0) {
                    // 刷新画面一览
                    layer.close(index);
                    layer.msg('删除成功，将关闭对话框．');
                    setTimeout("_endSave()", 1000)
                } else {
                    layer.msg(data.msg + ' code=' + data.code);
                }
            }
        });
    }, function() {
        // 无操作
    });
}

// 关闭对话框,刷新用户一览(当前分页)
function _endSave() {
    $('#page_grid').datagrid('reload', {});
    $('#editDlg').dialog('close');
}
