<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Редактировать пост</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<#include "header.ftlh">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow">
                <div class="card-header bg-warning text-dark">
                    <h4 class="mb-0">Редактировать пост</h4>
                </div>
                <div class="card-body">
                    <form action="/post/${post.id}/edit" method="post">
                        <div class="mb-3">
                            <label for="postText" class="form-label">Текст поста</label>
                            <textarea name="postText" id="postText" class="form-control" rows="6"
                                      required minlength="5">${post.postText}</textarea>
                        </div>

                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <a href="/topic/${post.topicId}" class="btn btn-secondary me-md-2">Отмена</a>
                            <button type="submit" class="btn btn-warning">Сохранить изменения</button>
                        </div>
                    </form>
                </div>
                <div class="card-footer text-muted small">
                    Создан: ${post.createdAt?string("dd.MM.yyyy HH:mm")}
                    <#if post.updatedAt??>
                        • Последнее изменение: ${post.updatedAt?string("dd.MM.yyyy HH:mm")}
                    </#if>
                </div>
            </div>
        </div>
    </div>
</div>

<#include "footer.ftlh">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>