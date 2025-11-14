<#include "header.ftlh">
<#assign pageTitle = "Регистрация" />

<div class="card" style="max-width: 450px; margin: 0 auto;">
    <h3 class="text-center" style="background: #e3f2fd; padding: 12px; border-radius: 8px;">Регистрация</h3>
    <#if error??>
        <div style="background: #ffebee; color: #cc0000; padding: 10px; border-radius: 6px; margin-bottom: 16px;">${error}</div>
    </#if>
    <form action="${contextPath}/auth/register" method="post">
        <div class="mb-2">
            <label>Логин</label>
            <input type="text" name="login" class="form-control" required minlength="3">
        </div>
        <div class="mb-2">
            <label>Email</label>
            <input type="email" name="email" class="form-control" required>
        </div>
        <div class="mb-2">
            <label>Дата рождения</label>
            <input type="date" name="birth_date" class="form-control" required>
        </div>
        <div class="mb-2">
            <label>Пароль</label>
            <input type="password" name="password" class="form-control" required minlength="6">
        </div>
        <div class="mb-2">
            <label>Повторите пароль</label>
            <input type="password" name="password2" class="form-control" required>
        </div>
        <button type="submit" class="btn btn-primary" style="width: 100%;">Зарегистрироваться</button>
    </form>
    <div class="text-center mt-2">
        <a href="${contextPath}/auth/login">Уже есть аккаунт? Войти</a>
    </div>
</div>

<#include "footer.ftlh">