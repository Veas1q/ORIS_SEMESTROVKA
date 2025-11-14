<#include "header.ftlh">
<#assign pageTitle = "Категории - Formach" />

<h1 class="mb-4">Категории форума</h1>

<div class="row">
    <#list categories![] as category>
        <div class="col-md-6 mb-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">
                        ${category.name!''}
                        <#if category.ageRestriction?? && category.ageRestriction gt 0>
                            <span class="badge bg-warning ms-1">${category.ageRestriction}+</span>
                        </#if>
                    </h5>
                    <p class="card-text">${category.description!''}</p>
                    <a href="${contextPath!''}/category/${category.id}" class="btn btn-outline-primary">Перейти</a>
                </div>
            </div>
        </div>
    </#list>
</div>

<#if !categories?? || categories?size == 0>
    <div class="alert alert-info text-center mt-4">
        Категорий пока нет.
    </div>
</#if>

<#include "footer.ftlh">