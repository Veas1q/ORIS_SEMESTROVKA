<!DOCTYPE html>
<html lang="ru" class="h-100">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${pageTitle!"Formach"}</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="d-flex flex-column h-100" data-context-path="${contextPath}">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${contextPath!''}/">Formach</a>
        <div class="navbar-nav me-auto">
            <a class="nav-link" href="${contextPath!''}/">Главная</a>
            <a class="nav-link" href="${contextPath!''}/category">Категории</a>
        </div>

        <form action="${contextPath!''}/search" method="get" class="d-flex mx-lg-4">
            <input type="text"
                   name="q"
                   class="form-control form-control-sm me-2"
                   placeholder="Поиск по топикам..."
                   value="<#if (q)??>${q?html}<#elseif (requestParameters?? && requestParameters.q?? && requestParameters.q?size gt 0)>${(requestParameters.q[0])?html}</#if>"
                   style="width: 300px;"
                   minlength="2"
                   maxlength="100">
            <button type="submit" class="btn btn-outline-light btn-sm">Найти</button>
        </form>

        <div class="d-flex align-items-center gap-3">
            <#if user??>
                <#if user.role == 'ADMIN' || user.role == 'MODERATOR'>
                    <a href="${contextPath}/admin/users" class="btn btn-sm btn-outline-light">Админка</a>
                </#if>
                <a href="${contextPath}/auth/logout" class="btn btn-sm btn-outline-danger">Выйти</a>
            <#else>
                <a href="${contextPath}/auth/login" class="btn btn-outline-light me-2">Войти</a>
                <a href="${contextPath}/auth/register" class="btn btn-outline-success">Регистрация</a>
            </#if>
        </div>
    </div>
</nav>

<main class="flex-grow-1">
    <div class="container py-4">