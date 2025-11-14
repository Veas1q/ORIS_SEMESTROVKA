<#include "header.ftlh">
<#assign pageTitle = "${category.name!''} - Formach" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1>${category.name!''}</h1>
    <#if user??>
        <a href="${contextPath!''}/topic/create?categoryId=${category.id!''}" class="btn btn-primary">Создать топик</a>
    </#if>
</div>

<#if category.description??>
    <div class="alert alert-info">${category.description}</div>
</#if>

<div class="list-group">
    <#list topics![] as topic>
        <#assign author = userService.findById(topic.userId)! />
        <a href="${contextPath!''}/topic/${topic.id}" class="list-group-item list-group-item-action
            <#if topic.pinned!false>bg-warning bg-opacity-25</#if>
            <#if topic.closed!false>opacity-75</#if>">
            <div class="d-flex w-100 justify-content-between align-items-start">
                <div class="flex-grow-1">
                    <h5 class="mb-1">${topic.title!''}</h5>
                    <div class="d-flex align-items-center text-muted small">
                        <span class="me-3">Просмотры: ${topic.viewCount!0}</span>
                        <span>
                            Автор:
                            <strong class="text-dark">${author?? ? string(author.login, "[Удалён]")}</strong>
                            • ${topic.createdAt!"—"}
                        </span>
                    </div>
                </div>
                <div class="text-end">
                    <#if topic.pinned!false>
                        <span class="badge bg-warning">Закреплён</span>
                    </#if>
                    <#if topic.closed!false>
                        <span class="badge bg-danger ms-1">Закрыт</span>
                    </#if>
                </div>
            </div>
        </a>
    </#list>
</div>

<#if !topics?? || topics?size == 0>
    <div class="alert alert-info text-center mt-4">
        В этой категории пока нет топиков. Будьте первым!
    </div>
</#if>

<#include "footer.ftlh">