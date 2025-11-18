<#include "header.ftlh">
<#assign pageTitle = "Категории - Formach" />

<h1 class="mb-4">Категории форума</h1>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div></div>
    <#if user?? && (user.role == 'ADMIN' || user.role == 'MODERATOR')>
        <a href="${contextPath!''}/category/create" class="btn btn-primary">
            Создать категорию
        </a>
    </#if>
</div>

<#if categories?? && categories?has_content>
    <div class="row">
        <#list categories as category>
            <div class="col-md-6 mb-4">
                <div class="card h-100 shadow-sm">
                    <div class="card-body d-flex flex-column">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <h5 class="card-title mb-0">
                                ${category.name}
                                <#if category.ageRestriction?? && category.ageRestriction gt 0>
                                    <span class="badge bg-warning ms-2">${category.ageRestriction}+</span>
                                </#if>
                            </h5>

                            <!-- КНОПКА УДАЛЕНИЯ — ТОЛЬКО ДЛЯ АДМИНА -->
                            <#if user?? && user.role == 'ADMIN'>
                                <form action="${contextPath!''}/category/${category.id}/delete"
                                      method="post"
                                      class="d-inline mb-0"
                                      onsubmit="return confirm('УДАЛИТЬ КАТЕГОРИЮ «${category.name}»?\n\nВсе топики, посты, вложения и реакции будут УДАЛЕНЫ НАВСЕГДА!\n\nЭто действие НЕЛЬЗЯ отменить.');">
                                    <button type="submit"
                                            class="btn btn-danger btn-sm"
                                            title="Удалить категорию навсегда">
                                        Удалить
                                    </button>
                                </form>
                            </#if>
                        </div>

                        <p class="card-text text-muted flex-grow-1">
                            ${category.description!''}
                            <#if !category.description?? || category.description == ''>
                                <em>Описание отсутствует</em>
                            </#if>
                        </p>

                        <div class="mt-auto">
                            <a href="${contextPath!''}/category/${category.id}"
                               class="btn btn-outline-primary w-100">
                                Перейти в категорию
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </#list>
    </div>
<#else>
    <div class="alert alert-info text-center mt-5 p-5">
        <h4>Категорий пока нет</h4>
        <p class="mb-0">Администратор или модератор может создать первую категорию.</p>
    </div>
</#if>

<#include "footer.ftlh">