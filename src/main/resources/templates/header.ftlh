<!DOCTYPE html>
<html lang="ru" class="h-100">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${pageTitle!"Formach"}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        html, body { height: 100%; }
        body { display: flex; flex-direction: column; }
        main { flex: 1 0 auto; }
        .navbar .container { padding-left: 15px; padding-right: 15px; }
        .navbar-brand { margin-right: 1.5rem; }
        .navbar-nav .nav-link { padding-left: 0.5rem; padding-right: 0.5rem; }
    </style>
</head>
<body class="d-flex flex-column h-100">
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="${contextPath!''}/">Formach</a>
        <div class="navbar-nav me-auto">
            <a class="nav-link" href="${contextPath!''}/">Главная</a>
            <a class="nav-link" href="${contextPath!''}/category">Категории</a>
        </div>
        <div class="navbar-nav ms-auto">
            <#if user??>
                <span class="nav-link">Привет, ${user.login!''}!</span>
                <a class="nav-link" href="${contextPath!''}/auth/logout">Выйти</a>
            <#else>
                <a class="nav-link" href="${contextPath!''}/auth/login">Войти</a>
                <a class="nav-link" href="${contextPath!''}/auth/register">Регистрация</a>
            </#if>
        </div>
    </div>
</nav>

<main class="flex-shrink-0">
    <div class="container mt-4">