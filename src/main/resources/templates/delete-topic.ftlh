<#include "header.ftlh">
<#assign pageTitle = "Удалить топик?" />

<div class="card text-center" style="max-width: 500px; margin: 0 auto;">
    <h3>Удалить топик?</h3>
    <p><strong>"${topic.title}"</strong></p>
    <p style="color: #cc0000;">Это действие нельзя отменить.</p>
    <form action="${req.contextPath}/topic/${topic.id}/delete" method="post" style="display: inline;">
        <button type="submit" class="btn btn-danger">Да, удалить</button>
    </form>
    <a href="${req.contextPath}/topic/${topic.id}" class="btn btn-secondary" style="margin-left: 8px;">Отмена</a>
</div>

<#include "footer.ftlh">