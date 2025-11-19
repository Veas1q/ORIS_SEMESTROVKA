<#include "header.ftlh">
<#assign pageTitle = "Создать категорию" />

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">

            <h1 class="mb-4">Создать новую категорию</h1>

            <#if error??>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <strong>Ошибка:</strong> ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </#if>

            <!-- ФОРМА -->
            <form method="post" action="${contextPath!''}/category/create" class="needs-validation" novalidate>
                <div class="mb-3">
                    <label for="name" class="form-label">
                        Название <span class="text-danger">*</span>
                    </label>
                    <input type="text"
                           class="form-control ${(nameError??)?then('is-invalid', '')}"
                           id="name"
                           name="name"
                           value="${(name!'')?html}"
                           required
                           maxlength="100"
                           minlength="3"
                           placeholder="Например: Игры">
                    <div class="invalid-feedback">
                        Введите название от 3 до 100 символов.
                    </div>
                </div>

                <div class="mb-3">
                    <label for="description" class="form-label">Описание</label>
                    <textarea class="form-control"
                              id="description"
                              name="description"
                              rows="4"
                              maxlength="500"
                              placeholder="Краткое описание категории...">${(description!'')?html}</textarea>
                    <div class="form-text">Макс. 500 символов. Опционально.</div>
                </div>

                <div class="mb-4">
                    <label for="ageRestriction" class="form-label">Возрастное ограничение</label>
                    <input type="number"
                           class="form-control ${(ageRestrictionError??)?then('is-invalid', '')}"
                           id="ageRestriction"
                           name="ageRestriction"
                           value="${(ageRestriction!'')}"
                           min="0"
                           max="18"
                           placeholder="18"
                           step="1">
                    <div class="form-text">Оставьте пустым — без ограничения. Диапазон: 0–18</div>
                    <#if ageRestrictionError??>
                        <div class="invalid-feedback d-block">${ageRestrictionError}</div>
                    </#if>
                </div>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-success btn-lg">
                        Создать категорию
                    </button>
                    <a href="${contextPath!''}/category" class="btn btn-outline-secondary btn-lg">
                        Отмена
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    (function () {
        'use strict';
        const forms = document.querySelectorAll('.needs-validation');
        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    })();
</script>

<#include "footer.ftlh">