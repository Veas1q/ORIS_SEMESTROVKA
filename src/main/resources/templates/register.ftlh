<#include "header.ftlh">
<#assign pageTitle = "Регистрация" />

<div class="card mx-auto" style="max-width: 450px;">
    <div class="card-body">
        <h3 class="card-title text-center mb-4">Регистрация</h3>

        <#if error??>
            <div class="alert alert-danger">${error}</div>
        </#if>

        <form action="${contextPath}/auth/register" method="post" class="needs-validation" novalidate>
            <div class="mb-3">
                <label class="form-label">Логин</label>
                <input type="text" name="login" class="form-control" required minlength="3">
            </div>
            <div class="mb-3">
                <label class="form-label">Email</label>
                <input type="email" name="email" class="form-control" required>
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
            <button type="submit" class="btn btn-primary w-100">Зарегистрироваться</button>
        </form>

        <div class="text-center mt-3">
            <a href="${contextPath}/auth/login">Уже есть аккаунт? Войти</a>
        </div>
    </div>
</div>

<#include "footer.ftlh">