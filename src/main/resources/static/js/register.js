$(document).ready(function() {
    $('#submit-button').click(function() {
        let username = $('#username').val();
        let email = $('#email').val();
        let password = $('#password').val();

        let encryptedPassword = CryptoJS.SHA512(password).toString();

        $.ajax({
            url: '/api/register',
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({
                username: username,
                email: email,
                password: encryptedPassword
            }),
            success: function(response) {
                if (response.success) {
                    alert('注册成功，请登录!');
                    window.location.href = 'login.html';
                } else {
                    alert(response.message);
                }
            },
            error: function(xhr, status, error) {
                alert('请求异常:' + error);
            }
        });
    });
});
