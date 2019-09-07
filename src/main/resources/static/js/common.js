$(function () {
    $( "#dialog" ).dialog({ autoOpen: false });
});

var DIALOG_WIDTH = 400;

//确认弹窗
function dialog(msg ,confirmFn) {
    //重新定义弹窗
    $("#dialog").dialog({
        resizable: false,
        height: "auto",
        width: DIALOG_WIDTH,
        modal: true,
        buttons: {
            "确定": function() {
                console.info("确定");
                confirmFn.call();
                //关闭弹窗
                $(this).dialog("close");
            },
            "取消": function() {
                console.info("取消");
                //关闭弹窗
                $(this).dialog("close");
            }
        }
    });
    $("#dialog").html(msg);
    //打开弹窗
    $( "#dialog" ).dialog( "open" );
}

