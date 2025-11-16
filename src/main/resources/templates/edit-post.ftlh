<#include "header.ftlh">
<#assign pageTitle = "Редактировать пост" />

<div class="card" style="max-width: 600px; margin: 0 auto;">
    <h3>Редактировать пост</h3>
    <form action="${contextPath}/post/${post.id}/edit" method="post">
        <textarea name="postText" class="form-control" rows="6" required>${post.postText}</textarea>
        <div class="mt-2 text-right">
            <a href="${contextPath}/topic/${post.topicId}" class="btn btn-secondary">Отмена</a>
            <button type="submit" class="btn btn-warning">Сохранить</button>
        </div>
    </form>
    <div class="text-muted mt-2" style="font-size: 0.9em;">
        Создан: ${post.createdAt!"—"}
        <#if post.updatedAt??> • Изменён: ${post.createdAt!"—"}</#if>
    </div>
</div>

<#include "footer.ftlh">