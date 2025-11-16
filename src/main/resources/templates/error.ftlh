<#include "header.ftlh">
<div class="container mt-5 text-center">
    <div class="alert alert-danger">
        <h3>Ошибка</h3>
        <p>${error!"Неизвестная ошибка"}</p>
        <a href="${contextPath!''}/" class="btn btn-primary">На главную</a>
    </div>
</div>
<#include "footer.ftlh">