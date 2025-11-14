<#include "header.ftlh">
<#assign pageTitle = "Вход" />

<div class="card" style="max-width: 400px; margin: 0 auto;">
    <h3 class="text-center" style="background: #e8f5e8; padding: 12px; border-radius: 8px;">Вход</h3>
    <#if error??>
        <div style="background: #ffebee; color: #cc0000; padding: 10px; border-radius: 6px; margin-bottom: 16px;">${error}</div>
    </#if>
    <form action="${contextPath}/auth/login" method="post">
        <div class="mb-2">
            <label>Логин или Email</label>
            <input type="text" name="login" class="form-control" required autofocus>
        </div>
        <div class="mb-2">
            <label>Пароль</label>
            <input type="password" name="password" class="form-control" required>
        </div>
        <button type="submit" class="btn btn-primary" style="width: 100%;">Войти</button>
    </form>
    <div class="text-center mt-2">
        <a href="${contextPath}/auth/register">Нет аккаунта? Регистрация</a>
    </div>
</div>

<#include "footer.ftlh">