<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Категории - Форум</title>
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
    <h1>Категории форума</h1>

    <div class="row">
        <#list categories![] as category>
            <div class="col-md-6 mb-3">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">
                            ${category.name!''}
                            <#if category.ageRestriction?? && category.ageRestriction gt 0>
                                <span class="badge bg-warning">${category.ageRestriction}+</span>
                            </#if>
                        </h5>
                        <p class="card-text">${category.description!''}</p>
                        <a href="/category/${category.id}" class="btn btn-outline-primary">Перейти</a>
                    </div>
                </div>
            </div>
        </#list>
    </div>

    <#if categories?? && categories?size == 0>
        <div class="alert alert-info">
            Категорий пока нет.
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