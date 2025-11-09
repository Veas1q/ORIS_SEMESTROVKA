<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Главная - Форум</title>
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
    <h1>Главная страница форума</h1>

    <#if pinnedTopics?? && pinnedTopics?size gt 0>
        <div class="card mb-4">
            <div class="card-header bg-warning">
                <h5 class="mb-0">📌 Закрепленные топики</h5>
            </div>
            <div class="list-group list-group-flush">
                <#list pinnedTopics as topic>
                    <a href="/topic/${topic.id}" class="list-group-item list-group-item-action" style="background-color: #fff3cd;">
                        <div class="d-flex w-100 justify-content-between">
                            <h6 class="mb-1">${topic.title!''}</h6>
                            <small>👁️ ${topic.viewCount!0}</small>
                        </div>
                        <#if topic.closed!false>
                            <span class="badge bg-danger">Закрыт</span>
                        </#if>
                    </a>
                </#list>
            </div>
        </div>
    </#if>

    <div class="card">
        <div class="card-header">
            <h5 class="mb-0">🔥 Последние топики</h5>
        </div>
        <div class="list-group list-group-flush">
            <#list recentTopics![] as topic>
                <a href="/topic/${topic.id}" class="list-group-item list-group-item-action <#if topic.closed!false>opacity-75</#if>">
                    <div class="d-flex w-100 justify-content-between">
                        <h6 class="mb-1">${topic.title!''}</h6>
                        <small>👁️ ${topic.viewCount!0}</small>
                    </div>
                    <small class="text-muted">Создан ${(topic.createdAt?string('dd.MM.yyyy HH:mm'))!''}</small>
                    <#if topic.closed!false>
                        <span class="badge bg-danger">Закрыт</span>
                    </#if>
                </a>
            </#list>
        </div>
    </div>

    <#if recentTopics?? && recentTopics?size == 0>
        <div class="alert alert-info mt-3">
            Пока нет топиков. Будьте первым!
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