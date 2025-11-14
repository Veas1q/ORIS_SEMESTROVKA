<!DOCTYPE html>
<html>
<head><title>Удалить топик?</title></head>
<body>
<#include "header.ftlh">
<div class="container mt-5 text-center">
    <h3>Удалить топик "${topic.title}"?</h3>
    <p>Это действие нельзя отменить.</p>
    <form action="/topic/${topic.id}/delete" method="post" style="display:inline;">
        <button type="submit" class="btn btn-danger">Да, удалить</button>
    </form>
    <a href="/topic/${topic.id}" class="btn btn-secondary">Нет, оставить</a>
</div>
<#include "footer.ftlh">
</body>
</html>