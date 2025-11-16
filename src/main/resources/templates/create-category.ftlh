<#include "header.ftlh">
<#assign pageTitle = "Создать категорию" />

<div class="container mt-5">
    <h1>Создать новую категорию</h1>

    <#if error??>
        <div class="alert alert-danger">${error}</div>
    </#if>

    <form method="post" action="${contextPath!''}/category/create" class="mt-4">
        <div class="mb-3">
            <label for="name" class="form-label">Название <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="name" name="name"
                   value="${(name!'')?html}" required maxlength="100">
        </div>

        <div class="mb-3">
            <label for="description" class="form-label">Описание</label>
            <textarea class="form-control" id="description" name="description" rows="3">${(description!'')?html}</textarea>
        </div>

        <div class="mb-3">
            <label for="ageRestriction" class="form-label">Возрастное ограничение</label>
            <input type="number" class="form-control" id="ageRestriction" name="ageRestriction"
                   value="${(ageRestriction!'')}" min="0" max="18" placeholder="Например: 18">
            <div class="form-text">Оставьте пустым, если ограничения нет.</div>
        </div>

        <button type="submit" class="btn btn-success">Создать</button>
        <a href="${contextPath!''}/category" class="btn btn-secondary">Отмена</a>
    </form>
</div>

<#include "footer.ftlh">