<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Удалить пост?</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .delete-box { max-width: 600px; margin: 100px auto; }
        .post-preview { background: #f8f9fa; padding: 15px; border-radius: 8px; }
    </style>
</head>
<body>
<#include "header.ftlh">

<div class="container">
    <div class="delete-box card shadow border-danger">
        <div class="card-header bg-danger text-white text-center">
            <h3>Удалить пост навсегда?</h3>
        </div>
        <div class="card-body">
            <p class="text-center text-danger fw-bold">Это действие нельзя отменить!</p>

            <div class="post-preview mb-4">
                <p><strong>Автор:</strong> ${postAuthor.login}</p>
                <p><strong>Создан:</strong> ${post.createdAt!"—"}</p>
                <hr>
                <p>${post.postText}</p>
            </div>

            <div class="text-center">
                <form action="${contextPath}/post/${post.id}/delete" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-danger btn-lg px-5">
                        Да, удалить навсегда
                    </button>
                </form>
                <a href="${contextPath}/topic/${post.topicId}" class="btn btn-secondary btn-lg px-5 ms-3">
                    Нет, оставить
                </a>
            </div>
        </div>
    </div>
</div>

<#include "footer.ftlh">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>