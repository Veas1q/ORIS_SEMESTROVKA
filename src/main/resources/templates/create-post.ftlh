<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Новый ответ — ${topic.title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f8f9fa; }
        textarea { resize: vertical; }
    </style>
</head>
<body>
<#include "header.ftlh">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-10 col-lg-8">
            <div class="card shadow-lg border-0">
                <div class="card-header bg-success text-white text-center">
                    <h3 class="mb-0">
                        Ответить в топик: <strong>${topic.title}</strong>
                    </h3>
                </div>
                <div class="card-body p-4">
                    <form action="${contextPath!''}/post/create" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="topicId" value="${topic.id}">

                        <div class="mb-4">
                            <label for="postText" class="form-label fs-5">
                                Ваш ответ
                            </label>
                            <textarea name="postText"
                                      id="postText"
                                      class="form-control"
                                      rows="8"
                                      placeholder="Напишите что-нибудь полезное..."
                                      required
                                      minlength="10"
                                      maxlength="5000"></textarea>

                            <div class="form-text text-muted mt-2">
                                Минимум 10 символов • Максимум 5000
                            </div>
                        </div>

                        <div class="mb-4">
                            <label for="file" class="form-label fs-5">Прикрепить файлы</label>
                            <input type="file" name="file" id="file" class="form-control" multiple
                                   accept="image/*, .pdf, .zip, .doc, .docx">
                            <div class="form-text text-muted mt-2">
                                До 10 МБ каждый. Можно несколько.
                            </div>
                        </div>

                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <a href="${contextPath!''}/topic/${topic.id}" class="btn btn-secondary me-md-2">
                                Отмена
                            </a>
                            <button type="submit" class="btn btn-success btn-lg">
                                Отправить ответ
                            </button>
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