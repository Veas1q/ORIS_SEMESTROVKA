<#include "header.ftlh">
<#assign pageTitle = "Поиск: ${query!''}" />

<div class="container mt-5">
    <h2>Поиск топиков: <strong>"${query!''}"</strong></h2>
    <p class="text-muted mb-4">
        Найдено: <strong>${results?size}</strong> топиков
    </p>

    <#if results?has_content>
        <div class="list-group">
            <#list results as t>
                <a href="${contextPath!''}/topic/${t.id}" class="list-group-item list-group-item-action py-3">
                    <div class="d-flex w-100 justify-content-between align-items-start">
                        <div>
                            <h5 class="mb-1">${t.title}</h5>
                            <small class="text-muted">
                                Создано:
                                <#if t.createdAt??>
                                    ${t.createdAt?replace("T", " ")?substring(0,16)?replace("-", ".")}
                                <#else>
                                    неизвестно
                                </#if>
                            </small>
                        </div>
                        <small class="text-muted">ID: ${t.id}</small>
                    </div>
                </a>
            </#list>
        </div>
    <#else>
        <div class="alert alert-light border text-center py-5 mt-4">
            <h4>Ничего не найдено</h4>
            <p class="mb-0">По запросу <strong>"${query!''}"</strong> топиков нет.</p>
        </div>
    </#if>

    <div class="mt-4">
        <a href="${contextPath!''}/" class="btn btn-secondary">На главную</a>
    </div>
</div>

<#include "footer.ftlh">