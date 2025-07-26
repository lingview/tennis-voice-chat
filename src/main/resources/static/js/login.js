$(document).ready(function() {
    $('#submit-button').click(function() {
        let username = $('#username').val();
        let password = $('#password').val();
        let encryptedPassword = CryptoJS.SHA512(password).toString();
        $.ajax({
            url: '/api/login',
            type: 'POST',
            dataType: 'json',
            data: {
                username: username,
                password: encryptedPassword
            },
            success: function(response) {
                if (response.success) {
                    localStorage.setItem("token", response.token);
                    alert('登录成功!');
                    window.location.href = 'index.html';
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
