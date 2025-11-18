<#include "header.ftlh">
<#assign pageTitle = "Удалить топик?" />

<div class="card mx-auto text-center" style="max-width: 500px;">
    <div class="card-body">
        <h3>Удалить топик?</h3>
        <p><strong>"${topic.title}"</strong></p>
        <p class="text-danger fw-bold">Это действие нельзя отменить.</p>
        <form action="${contextPath}/topic/${topic.id}/delete" method="post" class="d-inline">
            <button type="submit" class="btn btn-danger">Да, удалить</button>
        </form>
        <a href="${contextPath}/topic/${topic.id}" class="btn btn-secondary ms-3">Отмена</a>
    </div>
</div>

<#include "footer.ftlh">