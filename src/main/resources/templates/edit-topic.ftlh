<!DOCTYPE html>
<html>
<head><title>Редактировать топик</title></head>
<body>
<#include "header.ftlh">
<div class="container mt-5">
    <h2>Редактировать топик</h2>
    <form action="/topic/${topic.id}/edit" method="post">
        <div class="mb-3">
            <label>Название</label>
            <input type="text" name="title" value="${topic.title}" class="form-control" required>
        </div>
        <div class="mb-3">
            <label>Категория</label>
            <select name="categoryId" class="form-select">
                <#list categories as cat>
                    <option value="${cat.id}" ${cat.id == topic.categoryId?then('selected','')}>${cat.name}</option>
                </#list>
            </select>
        </div>
        <button type="submit" class="btn btn-success">Сохранить</button>
        <a href="/topic/${topic.id}" class="btn btn-secondary">Отмена</a>
    </form>
</div>
<#include "footer.ftlh">
</body>
</html>