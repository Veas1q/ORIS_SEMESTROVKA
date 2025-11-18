<#include "header.ftlh">
<#assign pageTitle = "Создать топик" />

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card shadow">
                <div class="card-header bg-primary text-white">
                    <h3 class="mb-0">Создать новый топик</h3>
                </div>
                <div class="card-body">
                    <#if error??><div class="alert alert-danger">${error}</div></#if>

                    <form action="${contextPath}/topic/create" method="post" class="needs-validation" novalidate>
                        <input type="hidden" name="from_category" value="${fromCategoryId!'1'}">

                        <div class="mb-3">
                            <label class="form-label">Название топика</label>
                            <input type="text" name="title" class="form-control" required minlength="3" maxlength="100"
                                   value="${(title)!''}" placeholder="Введите заголовок...">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Категория</label>
                            <select name="categoryId" class="form-select" required>
                                <#list categories as cat>
                                    <option value="${cat.id}" <#if fromCategoryId?? && fromCategoryId == cat.id?string>selected</#if>>${cat.name}</option>
                                </#list>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Возрастное ограничение</label>
                            <select name="ageRestriction" class="form-select">
                                <option value="0"  <#if ageRestriction?? && ageRestriction == "0">selected</#if>>0+ (для всех)</option>
                                <option value="6"  <#if ageRestriction?? && ageRestriction == "6">selected</#if>>6+</option>
                                <option value="12" <#if ageRestriction?? && ageRestriction == "12">selected</#if>>12+</option>
                                <option value="16" <#if ageRestriction?? && ageRestriction == "16">selected</#if>>16+</option>
                                <option value="18" <#if ageRestriction?? && ageRestriction == "18">selected</#if>>18+</option>
                            </select>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary btn-lg">Создать топик</button>
                            <a href="${contextPath}/category/${fromCategoryId!'1'}" class="btn btn-secondary btn-lg">Назад</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<#include "footer.ftlh">