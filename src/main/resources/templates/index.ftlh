<#include "header.ftlh">
<#assign pageTitle = "Formach" />

<h1 class="mb-4">Последние топики</h1>

<div class="list-group">
    <#list topics![] as topic>
        <a href="${contextPath!''}/topic/${topic.id}" class="list-group-item list-group-item-action">
            <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">${topic.title!''}</h5>
                <small class="text-muted">Просмотры: ${topic.viewCount!0}</small>
            </div>
            <p class="mb-1">Создан: ${topic.createdAt!"—"}</p>
        </a>
    </#list>
</div>

<#if !topics?? || topics?size == 0>
    <div class="alert alert-info text-center mt-4">
        Топиков пока нет. <a href="${contextPath!''}/category" class="alert-link">Перейдите в категории</a> и создайте первый!
    </div>
</#if>

<#include "footer.ftlh">