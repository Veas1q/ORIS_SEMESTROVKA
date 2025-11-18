<#include "header.ftlh">
<#assign pageTitle = "Редактировать пост" />

<div class="card mx-auto" style="max-width: 600px;">
    <div class="card-body">
        <h3 class="card-title">Редактировать пост</h3>
        <form action="${contextPath}/post/${post.id}/edit" method="post">
            <textarea name="postText" class="form-control" rows="6" required>${post.postText}</textarea>
            <div class="mt-3 text-end">
                <a href="${contextPath}/topic/${post.topicId}" class="btn btn-secondary">Отмена</a>
                <button type="submit" class="btn btn-warning ms-2">Сохранить</button>
            </div>
        </form>
        <div class="text-muted small mt-3">
            Создан: ${post.createdAt!"—"}
            <#if post.updatedAt??> • Изменён: ${post.updatedAt!"—"}</#if>
        </div>
    </div>
</div>

<#include "footer.ftlh">