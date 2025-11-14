<#include "header.ftlh">
<#assign pageTitle = "Редактировать топик" />

<div class="card" style="max-width: 600px; margin: 0 auto;">
    <h3>Редактировать топик</h3>
    <form action="${contextPath}/topic/${topic.id}/edit" method="post">
        <div class="mb-2">
            <label>Название</label>
            <input type="text" name="title" value="${topic.title}" class="form-control" required>
        </div>
        <div class="mb-2">
            <label>Категория</label>
            <select id="categoryId" name="categoryId" class="form-select" required>
                <#if categories?? && (categories?size > 0)>
                    <#list categories as cat>
                        <option value="${cat.id}"
                                <#if fromCategoryId?? && fromCategoryId == cat.id?string>selected</#if>>
                            ${cat.name}
                        </option>
                    </#list>
                <#else>
                    <option value="">Нет доступных категорий</option>
                </#if>
            </select>
        </div>
        <button type="submit" class="btn btn-primary">Сохранить</button>
        <a href="${contextPath}/topic/${topic.id}" class="btn btn-secondary">Отмена</a>
    </form>
</div>

<#include "footer.ftlh">