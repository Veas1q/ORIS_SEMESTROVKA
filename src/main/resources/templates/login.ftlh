<#include "header.ftlh">
<#assign pageTitle = "Вход" />

<div class="card mx-auto" style="max-width: 400px;">
    <div class="card-body">
        <h3 class="card-title text-center mb-4">Вход</h3>

        <#if error??>
            <div class="alert alert-danger">${error}</div>
        </#if>

        <form action="${contextPath}/auth/login" method="post" class="needs-validation" novalidate>
            <div class="mb-3">
                <label class="form-label">Логин или Email</label>
                <input type="text" name="login" class="form-control" required autofocus>
            </div>
            <div class="mb-3">
                <label class="form-label">Пароль</label>
                <input type="password" name="password" class="form-control" required>
            </div>
            <button type="submit" class="btn btn-primary w-100">Войти</button>
        </form>

        <div class="text-center mt-3">
            <a href="${contextPath}/auth/register">Нет аккаунта? Регистрация</a>
        </div>
    </div>
</div>

<#include "footer.ftlh">