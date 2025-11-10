<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${category.name!''} - Форум</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="">Форум</a>
        <div class="navbar-nav me-auto">
            <a class="nav-link" href="">Главная</a>
            <a class="nav-link" href="category/">Категории</a>
        </div>
        <div class="navbar-nav">
            <#if user??>
                <span class="nav-link">Привет, ${user.login!''}!</span>
                <a class="nav-link" href="auth/logout">Выйти</a>
            <#else>
                <a class="nav-link" href="auth/login">Войти</a>
                <a class="nav-link" href="auth/register">Регистрация</a>
            </#if>
        </div>
    </div>
</nav>

<div class="container mt-4">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="">Главная</a></li>
            <li class="breadcrumb-item"><a href="category/">Категории</a></li>
            <li class="breadcrumb-item active">${category.name!''}</li>
        </ol>
    </nav>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>${category.name!''}</h1>
        <#if user??>
            <a href="/topic/create?categoryId=${category.id!''}" class="btn btn-primary">Создать топик</a>
        </#if>
    </div>

    <#if category.description??>
        <div class="alert alert-info">${category.description}</div>
    </#if>

    <div class="list-group">
        <#list topics![] as topic>
            <a href="/topic/${topic.id}" class="list-group-item list-group-item-action
                <#if topic.pinned!false>bg-warning bg-opacity-25</#if>
                <#if topic.closed!false>opacity-75</#if>">
                <div class="d-flex w-100 justify-content-between">
                    <h5 class="mb-1">${topic.title!''}</h5>
                    <div>
                        <small class="text-muted">👁️ ${topic.viewCount!0}</small>
                        <#if topic.pinned!false>
                            <span class="badge bg-warning ms-1">Закреплен</span>
                        </#if>
                        <#if topic.closed!false>
                            <span class="badge bg-danger ms-1">Закрыт</span>
                        </#if>
                    </div>
                </div>
                <p class="mb-1">Создан: ${(topic.createdAt?string('dd.MM.yyyy HH:mm'))!''}</p>
            </a>
        </#list>
    </div>

    <#if topics?? && topics?size == 0>
        <div class="alert alert-info text-center mt-4">
            В этой категории пока нет топиков. Будьте первым!
        </div>
    </#if>
</div>

<footer class="bg-dark text-light mt-5 py-4">
    <div class="container text-center">
        <p>&copy; 2024 Форум. Все права защищены.</p>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>