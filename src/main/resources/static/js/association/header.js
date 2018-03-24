$(function () {
    $.ajax({
        url: '/association/header',
        type: 'post',
        dataType: 'html',
        error: function () {
            alert('error');
        },
        success: function (data) {
            $("#header").html(data);
        }
    });
});