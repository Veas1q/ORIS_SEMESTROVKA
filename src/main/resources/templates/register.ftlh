<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Регистрация — ITIS Forum</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
</head>
<body>
<#include "header.ftlh">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow">
                <div class="card-header bg-primary text-white text-center">
                    <h3>Регистрация</h3>
                </div>
                <div class="card-body">
                    <#if error??>
                        <div class="alert alert-danger">${error}</div>
                    </#if>

                    <form id="registerForm" action="${req.contextPath}/auth/register" method="post">
                        <div class="mb-3">
                            <label class="form-label">Логин</label>
                            <input type="text" name="login" class="form-control" required minlength="3" maxlength="20">
                            <div id="loginFeedback" class="form-text"></div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" name="email" class="form-control" required>
                            <div id="emailFeedback" class="form-text"></div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Дата рождения</label>
                            <input type="date" name="birth_date" class="form-control" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Пароль</label>
                            <input type="password" name="password" class="form-control" required minlength="6">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Повторите пароль</label>
                            <input type="password" name="password2" class="form-control" required>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary btn-lg">Зарегистрироваться</button>
                        </div>
                    </form>

                    <div class="text-center mt-3">
                        <a href="${req.contextPath}/auth/login">Уже есть аккаунт? Войти</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        const loginInput = $('[name="login"]');
        const emailInput = $('[name="email"]');

        loginInput.on('blur', function() {
            const login = $(this).val().trim();
            if (login.length >= 3) {
                $.post('${req.contextPath}/auth/check-login', { login: login }, function(data) {
                    if (data.available) {
                        $('#loginFeedback').text('Логин свободен').css('color', 'green');
                    } else {
                        $('#loginFeedback').text('Логин занят').css('color', 'red');
                    }
                });
            }
        });

        emailInput.on('blur', function() {
            const email = $(this).val().trim();
            if (email.includes('@')) {
                $.post('${req.contextPath}/auth/check-email', { email: email }, function(data) {
                    if (data.available) {
                        $('#emailFeedback').text('Email свободен').css('color', 'green');
                    } else {
                        $('#emailFeedback').text('Email уже используется').css('color', 'red');
                    }
                });
            }
        });

        $('#registerForm').on('submit', function(e) {
            const pass1 = $('[name="password"]').val();
            const pass2 = $('[name="password2"]').val();
            if (pass1 !== pass2) {
                alert('Пароли не совпадают!');
                e.preventDefault();
            }
        });
    });
</script>

<#include "footer.ftlh">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>