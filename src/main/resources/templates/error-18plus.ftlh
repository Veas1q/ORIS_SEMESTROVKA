<#include "header.ftlh">
<#assign pageTitle = "Доступ запрещён" />

<div class="container mt-5 text-center">
    <div class="alert alert-warning">
        <h4>18+ Контент</h4>
        <p>${error!''}</p>
    </div>

    <#if redirectUrl??>
        <a href="${redirectUrl}" class="btn btn-primary">Войти</a>
    <#else>
        <a href="${contextPath!''}/login" class="btn btn-primary">Войти</a>
    </#if>
    <a href="${contextPath!''}/category" class="btn btn-secondary ms-2">На главную</a>
</div>

<#include "footer.ftlh">