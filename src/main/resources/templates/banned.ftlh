<#include "header.ftlh">
<#assign pageTitle = "Вы заблокированы" />

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="alert alert-danger text-center">
                <h2 class="alert-heading">Вы заблокированы</h2>
                <hr>
                <p><strong>Причина:</strong> ${banReason!"Нарушение правил"}</p>

                <#if bannedUntil??>
                    <p><strong>До:</strong>
                        ${bannedUntil?replace('T', ' ')?substring(0, 16)?replace('-', '.')?replace(' ', ' в ')}
                    </p>
                <#else>
                    <p class="text-danger"><strong>Бессрочно</strong></p>
                </#if>

                <a href="${contextPath!''}/auth/logout" class="btn btn-outline-danger">Выйти</a>
            </div>
        </div>
    </div>
</div>

<#include "footer.ftlh">