<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Вход — ITIS Forum</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<#include "header.ftlh">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card shadow">
                <div class="card-header bg-success text-white text-center">
                    <h3>Вход в аккаунт</h3>
                </div>
                <div class="card-body">
                    <#if error??>
                        <div class="alert alert-danger">${error}</div>
                    </#if>

                    <form action="${req.contextPath}/auth/login" method="post">
                        <div class="mb-3">
                            <label class="form-label">Логин или Email</label>
                            <input type="text" name="login" class="form-control" required autofocus>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Пароль</label>
                            <input type="password" name="password" class="form-control" required>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-success btn-lg">Войти</button>
                        </div>
                    </form>

                    <div class="text-center mt-3">
                        <a href="${req.contextPath}/auth/register">Нет аккаунта? Зарегистрироваться</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<#include "footer.ftlh">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>