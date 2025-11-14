<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${topic.title} - Форум</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <style>
        .like-button button { transition: all 0.2s; }
        .like-button button:hover { transform: scale(1.2); }
    </style>
</head>
<body>
<#include "header.ftlh">

<div class="container mt-4">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="/">Главная</a></li>
            <li class="breadcrumb-item"><a href="/category/${topic.categoryId}">${category.name}</a></li>
            <li class="breadcrumb-item active">${topic.title}</li>
        </ol>
    </nav>

    <div class="card mb-4 shadow-sm">
        <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
            <h4 class="mb-0">${topic.title}</h4>
            <#if user?? && user.id == topic.userId>
                <div>
                    <a href="${req.contextPath}/topic/${topic.id}/edit" class="btn btn-warning btn-sm">Редактировать</a>
                    <a href="${req.contextPath}/topic/${topic.id}/delete" class="btn btn-danger btn-sm"
                       onclick="return confirm('Удалить топик навсегда?')">Удалить</a>
                </div>
            </#if>
        </div>
        <div class="card-body">
            <p><strong>Автор:</strong> ${topicAuthor.login} • ${topic.createdAt!"—"}</p>
            <p class="lead">${topic.firstPostText!''}</p>
        </div>
    </div>

    <h5>Ответы (${posts?size})</h5>
    <#list posts as post>
        <div class="card mb-3 shadow-sm">
            <div class="card-body">
                <div class="d-flex justify-content-between">
                    <div>
                        <strong>${postAuthorMap[post.id].login}</strong>
                        <small class="text-muted">• ${topic.createdAt!"—"}</small>
                        <p class="mt-2">${post.postText}</p>
                    </div>
                    <div class="text-end">
                        <div class="like-button" data-post-id="${post.id}">
                            <button class="btn btn-sm ${post.likedByUser?then('btn-danger','btn-outline-danger')}"
                                    title="Лайкнуть">
                                <span class="heart">❤️</span>
                                <span class="count">${post.likesCount}</span>
                            </button>
                        </div>
                        <#if user?? && (user.id == post.userId || user.role == 'ADMIN')>
                            <div class="mt-2">
                                <a href="${req.contextPath}/post/${post.id}/edit" class="btn btn-sm btn-outline-secondary">Редактировать</a>
                                <a href="${req.contextPath}/post/${post.id}/delete" class="btn btn-sm btn-outline-danger"
                                   onclick="return confirm('Удалить пост?')">Удалить</a>
                            </div>
                        </#if>
                    </div>
                </div>
            </div>
        </div>
    </#list>

    <#if user??>
        <div class="card mt-5 shadow">
            <div class="card-header bg-success text-white">Написать ответ</div>
            <div class="card-body">
                <form action="${req.contextPath}/post/create" method="post">
                    <input type="hidden" name="topicId" value="${topic.id}">
                    <textarea name="postText" class="form-control" rows="4" required
                              placeholder="Ваш ответ..."></textarea>
                    <button type="submit" class="btn btn-success mt-3">Отправить</button>
                </form>
            </div>
        </div>
    <#else>
        <div class="alert alert-info text-center">
            <a href="${req.contextPath}/auth/login">Войдите</a>, чтобы ответить
        </div>
    </#if>
</div>

<script>
    $(document).on('click', '.like-button button', function() {
        const btn = $(this);
        const postId = btn.closest('.like-button').data('post-id');

        $.post('/post/' + postId + '/like')
            .done(function(data) {
                btn.find('.count').text(data.count);
                btn.toggleClass('btn-danger btn-outline-danger');
            })
            .fail(function() {
                alert('Ошибка! Возможно, вы не авторизованы.');
            });
    });
</script>

<#include "footer.ftlh">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>