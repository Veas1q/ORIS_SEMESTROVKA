<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="${req.contextPath}/home">Форум</a>
        <div class="navbar-nav me-auto">
            <a class="nav-link" href="${req.contextPath}/home">Главная</a>
            <a class="nav-link" href="${req.contextPath}/category">Категории</a>
        </div>
        <div class="navbar-nav">
            <#if user??>
                <span class="nav-link">Привет, ${user.login!''}!</span>
                <a class="nav-link" href="${req.contextPath}/auth/logout">Выйти</a>
            <#else>
                <a class="nav-link" href="${req.contextPath}/auth/login">Войти</a>
                <a class="nav-link" href="${req.contextPath}/auth/register">Регистрация</a>
            </#if>
        </div>
    </div>
</nav>