<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Создать топик — ITIS Forum</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<#include "header.ftlh">

<div class="container mt-5 mb-5">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card shadow">
                <div class="card-header bg-primary text-white">
                    <h3 class="mb-0">Создать новый топик</h3>
                </div>
                <div class="card-body">
                    <#if error??>
                        <div class="alert alert-danger">${error}</div>
                    </#if>

                    <form action="${req.contextPath}/topic/create" method="post">
                        <!-- Скрытое поле — откуда пришли -->
                        <input type="hidden" name="from_category" value="${fromCategoryId!'1'}">

                        <div class="mb-3">
                            <label for="title" class="form-label">Название топика</label>
                            <input type="text"
                                   id="title"
                                   name="title"
                                   class="form-control"
                                   required
                                   minlength="3"
                                   maxlength="100"
                                   placeholder="Введите заголовок...">
                        </div>

                        <div class="mb-3">
                            <label for="categoryId" class="form-label">Категория</label>
                            <select id="categoryId" name="categoryId" class="form-select" required>
                                <#if categories?? && (categories?size > 0)>
                                    <#list categories as cat>
                                        <option value="${cat.id}"
                                                <#if fromCategoryId?? && fromCategoryId == cat.id?string>selected</#if>>
                                            ${cat.name}
                                        </option>
                                    </#list>
                                <#else>
                                    <option value="">Нет доступных категорий</option>
                                </#if>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label for="ageRestriction" class="form-label">Возрастное ограничение</label>
                            <select id="ageRestriction" name="ageRestriction" class="form-select" required>
                                <option value="0+" <#if (topic.ageRestriction)?? && topic.ageRestriction == "0+">selected</#if>>0+ (для всех)</option>
                                <option value="12+" <#if (topic.ageRestriction)?? && topic.ageRestriction == "12+">selected</#if>>12+ (подростки)</option>
                                <option value="16+" <#if (topic.ageRestriction)?? && topic.ageRestriction == "16+">selected</#if>>16+ (взрослые)</option>
                                <option value="18+" <#if (topic.ageRestriction)?? && topic.ageRestriction == "18+">selected</#if>>18+ (только взрослые)</option>
                            </select>
                            <div class="form-text">Выберите минимальный возраст для просмотра</div>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary btn-lg">
                                Создать топик
                            </button>
                            <a href="${req.contextPath}/category/${fromCategoryId!'1'}"
                               class="btn btn-secondary btn-lg">
                                Назад
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<#include "footer.ftlh">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html><!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Создать топик — ITIS Forum</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<#include "header.ftlh">

<div class="container mt-5 mb-5">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card shadow">
                <div class="card-header bg-primary text-white">
                    <h3 class="mb-0">Создать новый топик</h3>
                </div>
                <div class="card-body">
                    <#if error??>
                        <div class="alert alert-danger">${error}</div>
                    </#if>

                    <form action="${req.contextPath}/topic/create" method="post">
                        <!-- Скрытое поле — откуда пришли -->
                        <input type="hidden" name="from_category" value="${fromCategoryId!'1'}">

                        <div class="mb-3">
                            <label for="title" class="form-label">Название топика</label>
                            <input type="text"
                                   id="title"
                                   name="title"
                                   class="form-control"
                                   required
                                   minlength="3"
                                   maxlength="100"
                                   placeholder="Введите заголовок...">
                        </div>

                        <div class="mb-3">
                            <label for="categoryId" class="form-label">Категория</label>
                            <select id="categoryId" name="categoryId" class="form-select" required>
                                <#if categories?? && (categories?size > 0)>
                                    <#list categories as cat>
                                        <option value="${cat.id}"
                                                <#if fromCategoryId?? && fromCategoryId == cat.id?string>selected</#if>>
                                            ${cat.name}
                                        </option>
                                    </#list>
                                <#else>
                                    <option value="">Нет доступных категорий</option>
                                </#if>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label>Возрастное ограничение</label>
                            <select name="ageRestriction" class="form-select" required>
                                <option value="0">0+ (для всех)</option>
                                <option value="6">6+</option>
                                <option value="12">12+</option>
                                <option value="16">16+</option>
                                <option value="18">18+</option>
                            </select>
                            <small class="text-muted">Выберите минимальный возраст для просмотра</small>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary btn-lg">
                                Создать топик
                            </button>
                            <a href="${req.contextPath}/category/${fromCategoryId!'1'}"
                               class="btn btn-secondary btn-lg">
                                Назад
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<#include "footer.ftlh">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>