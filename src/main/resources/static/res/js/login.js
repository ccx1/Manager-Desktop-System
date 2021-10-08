$(document).ready(function () {
    let usernameDom = document.getElementById("username");
    let psdDom = document.getElementById("password");
    let errorText = $('#error_text');
    function getUrlParams(variable) {
        var query = window.location.search.substring(1);
        var vars = query.split("&");
        for (var i = 0; i < vars.length; i++) {
            var pair = vars[i].split("=");
            if (pair[0] === variable) {
                return pair[1];
            }
        }
        return false;
    }

    $('#login').on('click', function () {
        $.ajax({
            type: 'get',
            url: '/manager/user/login',
            data: {
                username: usernameDom.value,
                password: psdDom.value
            },
            dataType: 'json',
            contentType: 'application/json;charset=utf8',
            cache: false,
            success(data) {
                if (data.code === 200) {
                    // let urlParams = getUrlParams("redirect");
                    // if (urlParams) {
                    //     // window.location.replace(decodeURIComponent(urlParams))
                    // }else {
                    //     // window.location.replace(`${ window.location.protocol}//${window.location.host}/admin/success`)
                    // }
                }else {
                    errorText.empty()
                    errorText.append(data.msg)
                }
            },
            error(xhr, type) {

            }
        });
    })

})
