<#include "header.ftlh">
<#assign pageTitle = topic.title />

<style>
    body {
        min-height: 100vh;
        background: #f8f9fa;
    }
    .reaction-container button {
        min-width: 80px;
    }
    .reaction-btn {
        transition: all 0.2s;
    }
    .reaction-btn:hover {
        transform: scale(1.1);
    }
    .card {
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }
    .list-group-item {
        border-left: 4px solid transparent;
        transition: all 0.2s;
    }
    .list-group-item:hover {
        border-left-color: #0d6efd;
        background-color: #f8f9fa;
    }
    .btn-success, .btn-danger {
        font-weight: bold;
    }
</style>

<div class="container py-4">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="m-0">${topic.title}</h1>
        <#if user?? && (user.id == topic.userId || user.role == 'ADMIN' || user.role == 'MODERATOR')>
            <div>
                <a href="${contextPath}/topic/${topic.id}/edit" class="btn btn-sm btn-outline-warning me-1">Редактировать</a>
                <form action="${contextPath}/topic/${topic.id}/delete" method="post" class="d-inline">
                    <button type="submit" class="btn btn-sm btn-outline-danger"
                            onclick="return confirm('Удалить топик и все ответы? Это нельзя отменить.')">
                        Удалить
                    </button>
                </form>
            </div>
        </#if>
    </div>

    <div class="card mb-4 border-primary">
        <div class="card-body">
            <p class="card-text mb-3">${topic.firstPostText!''}</p>
            <p class="text-muted small mb-0">
                <strong>Автор: ${(author.login)!"[Удалён]"}</strong>
                <#if topic.createdAt??>
                    • ${topic.createdAt?replace("T", " ")?substring(0, 16)}
                </#if>
            </p>
        </div>
    </div>

    <h4 class="mb-3">Ответы (${posts?size})</h4>

    <div class="list-group mb-4">
        <#list posts as post>
            <#assign postAuthor = userService.findById(post.userId)! />
            <div class="list-group-item p-4 bg-white border-bottom">
                <div class="d-flex justify-content-between align-items-start">
                    <div class="flex-grow-1">
                        <div class="d-flex align-items-center mb-2">
                            <strong class="me-2">${postAuthor?? ? string(postAuthor.login, "[Удалён]")}</strong>
                            <small class="text-muted">
                                <#if post.createdAt??>
                                    • ${post.createdAt?replace("T", " ")?substring(0, 16)}
                                </#if>
                            </small>
                        </div>
                        <p class="mb-3">${post.postText}</p>

                        <!-- Лайки и дизлайки -->
                        <div class="reaction-container d-flex gap-3 mb-3">
                            <button class="btn btn-outline-success reaction-btn"
                                    data-reaction="LIKE" data-post-id="${post.id}">
                                <i class="bi bi-hand-thumbs-up"></i> <span class="like-count">${post.likes!0}</span>
                            </button>
                            <button class="btn btn-outline-danger reaction-btn"
                                    data-reaction="DISLIKE" data-post-id="${post.id}">
                                <i class="bi bi-hand-thumbs-down"></i> <span class="dislike-count">${post.dislikes!0}</span>
                            </button>
                        </div>
                    </div>

                    <!-- КНОПКИ РЕДАКТИРОВАНИЯ И УДАЛЕНИЯ — ТОЛЬКО ДЛЯ АВТОРА/МОДЕРА/АДМИНА -->
                    <#if user?? && (user.id == post.userId || user.role == 'ADMIN' || user.role == 'MODERATOR')>
                        <div class="d-flex flex-column gap-2 ms-3">
                            <a href="${contextPath}/post/${post.id}/edit"
                               class="btn btn-sm btn-outline-warning" title="Редактировать">
                                <i class="bi bi-pencil"></i>
                            </a>
                            <form action="${contextPath}/post/${post.id}/delete" method="post" class="d-inline m-0">
                                <button type="submit"
                                        class="btn btn-sm btn-outline-danger"
                                        onclick="return confirm('Удалить этот пост навсегда?')"
                                        title="Удалить">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </form>
                        </div>
                    </#if>
                </div>
            </div>
        </#list>
    </div>

    <#if user??>
        <div class="card mt-5 border-success">
            <div class="card-header bg-success text-white">
                <h5 class="mb-0">Написать ответ</h5>
            </div>
            <div class="card-body">
                <form action="${contextPath}/post/create" method="post">
                    <input type="hidden" name="topicId" value="${topic.id}">
                    <textarea name="postText" class="form-control" rows="5" placeholder="Напишите что-нибудь полезное..." required></textarea>
                    <div class="text-end mt-3">
                        <button type="submit" class="btn btn-success btn-lg">Отправить</button>
                    </div>
                </form>
            </div>
        </div>
    <#else>
        <div class="alert alert-info text-center mt-5">
            <a href="${contextPath}/auth/login" class="alert-link">Войдите</a>, чтобы ответить в топике
        </div>
    </#if>
    <script>
        // 1. Получаем базовый путь приложения из атрибута data-context-path тела документа
        // Если атрибут не найден, используем пустую строку
        const contextPath = document.body.getAttribute("data-context-path") || "";

        // 2. Ждем полной загрузки DOM перед выполнением скрипта
        document.addEventListener("DOMContentLoaded", function () {

            // 3. Находим ВСЕ кнопки с классом "reaction-btn" на странице
            document.querySelectorAll(".reaction-btn").forEach(function (btn) {

                // 4. Добавляем обработчик клика на каждую кнопку
                btn.addEventListener("click", async function () {

                    // 5. Получаем ID поста из data-атрибута кнопки
                    const postId = this.dataset.postId;
                    // 6. Получаем тип реакции (LIKE/DISLIKE) из data-атрибута
                    const reaction = this.dataset.reaction;

                    try {
                        // 7. Отправляем AJAX-запрос на сервер
                        const response = await fetch(contextPath + "/post/" + postId + "/reaction", {
                            method: "POST", // Метод запроса - отправка данных
                            headers: {
                                "Content-Type": "application/x-www-form-urlencoded", // Тип передаваемых данных
                                "X-Requested-With": "XMLHttpRequest" // Помечаем как AJAX-запрос
                            },
                            body: "reaction=" + reaction // Передаем тип реакции в теле запроса
                        });

                        // 8. Проверяем успешность HTTP-ответа (статус 200-299)
                        if (!response.ok) {
                            alert("Ошибка сервера: " + response.status);
                            return; // Прерываем выполнение при ошибке
                        }

                        // 9. Парсим JSON-ответ от сервера
                        const data = await response.json();

                        // 10. Проверяем флаг успеха в ответе сервера
                        if (data.success) {
                            // 11. Обновляем счетчики лайков на странице
                            document.querySelector('[data-post-id="' + postId + '"][data-reaction="LIKE"] .like-count').textContent = data.likes;
                            document.querySelector('[data-post-id="' + postId + '"][data-reaction="DISLIKE"] .dislike-count').textContent = data.dislikes;
                        } else {
                            // 12. Показываем сообщение об ошибке от сервера
                            alert(data.message || "Не удалось поставить реакцию");
                        }
                    } catch (err) {
                        // 13. Обрабатываем ошибки сети или выполнения
                        console.error(err);
                        alert("Нет соединения с сервером");
                    }
                });
            });
            });
        });
    </script>
</div>

<#include "footer.ftlh">