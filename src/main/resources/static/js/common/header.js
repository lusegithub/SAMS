$(function () {
    $.ajax({
        url: '/header',
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