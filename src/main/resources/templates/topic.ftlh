<#include "header.ftlh">
<#assign pageTitle = topic.title />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="m-0">${topic.title}</h1>
    <#if user?? && (user.id == topic.userId || user.role == 'ADMIN')>
        <div>
            <a href="${contextPath!''}/topic/${topic.id}/edit" class="btn btn-sm btn-outline-warning me-1">
                Edit
            </a>
            <a href="${contextPath!''}/topic/${topic.id}/delete"
               onclick="return confirm('Удалить топик?')" class="btn btn-sm btn-outline-danger">
                Delete
            </a>
        </div>
    </#if>
</div>

<div class="card mb-4">
    <div class="card-body">
        <p class="card-text">${topic.firstPostText!''}</p>
        <p class="text-muted small mb-0">
            <strong class="text-dark">Автор: ${(author.login)!"[Удалён]"}</strong> • ${topic.createdAt!"—"}
        </p>
    </div>
</div>

<h4 class="mb-3">Ответы (${posts?size})</h4>

<div class="list-group mb-4">
    <#list posts as post>
        <#assign postAuthor = userService.findById(post.userId)! />
        <div class="list-group-item p-3">
            <div class="d-flex justify-content-between align-items-start">
                <div class="flex-grow-1 me-3">
                    <div class="d-flex align-items-center mb-1">
                        <strong class="text-primary me-2">${postAuthor?? ? string(postAuthor.login, "[Удалён]")}</strong>
                        <small class="text-muted">${post.createdAt!"—"}</small>
                    </div>
                    <p class="mb-0 mt-1">${post.postText}</p>
                </div>
                <#if post.attachments?? && (post.attachments?size > 0)>
                    <div class="mt-3 p-3 border rounded bg-light">
                        <strong>Вложения:</strong>
                        <div class="row row-cols-1 row-cols-md-3 g-3 mt-2">
                            <#list post.attachments as att>
                                <div class="col">
                                    <#if att.mimeType?starts_with("image/")>
                                        <a href="${contextPath}${att.filePath}" target="_blank">
                                            <img src="${contextPath}${att.filePath}"
                                                 class="img-fluid rounded"
                                                 style="max-height: 200px; object-fit: cover;">
                                        </a>
                                    <#else>
                                        <a href="${contextPath}${att.filePath}"
                                           target="_blank"
                                           class="btn btn-sm btn-outline-secondary d-block text-start">
                                            ${att.filename}<br>
                                            <small>${(att.fileSize!0)?string("#,##0")} байт</small>
                                        </a>
                                    </#if>
                                </div>
                            </#list>
                        </div>
                    </div>
                </#if>

                <div class="d-flex flex-column align-items-end gap-2">
                    <!-- ЛАЙК -->
                    <div class="d-flex align-items-center gap-1">
                        <button class="btn btn-sm btn-outline-success like-btn" data-post-id="${post.id}" data-reaction="LIKE">
                            ❤️
                        </button>
                        <span class="like-count badge bg-success text-white">${post.likesCount!0}</span>
                    </div>

                    <!-- ДИЗЛАЙК -->
                    <div class="d-flex align-items-center gap-1">
                        <button class="btn btn-sm btn-outline-danger dislike-btn" data-post-id="${post.id}" data-reaction="DISLIKE">
                            👎
                        </button>
                        <span class="dislike-count badge bg-danger text-white">${post.dislikesCount!0}</span>
                    </div>

                    <!-- КНОПКИ РЕД/УДАЛ -->
                    <#if user?? && (user.id == post.userId || user.role == 'ADMIN' || user.role == 'MODERATOR')>
                        <button type="button" class="btn btn-sm btn-outline-danger" data-bs-toggle="modal" data-bs-target="#deleteModal${post.id}">
                            Удалить
                        </button>

                        <!-- Modal -->
                        <div class="modal fade" id="deleteModal${post.id}" tabindex="-1">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header bg-danger text-white">
                                        <h5 class="modal-title">Удалить пост?</h5>
                                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                    </div>
                                    <div class="modal-body">
                                        <p>Это действие <strong>нельзя отменить</strong>.</p>
                                        <p><strong>Автор:</strong> ${userService.findById(post.userId).login!"[Удалён]"}</p>
                                        <p><strong>Текст:</strong> ${post.postText?html}</p>
                                    </div>
                                    <div class="modal-footer">
                                        <form action="${contextPath!''}/post/${post.id}/delete" method="post" class="d-inline">
                                            <button type="submit" class="btn btn-danger">Да, удалить</button>
                                        </form>
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </#if>
                </div>
            </div>
        </div>
    </#list>
</div><#if user??>
<div class="card mt-5 shadow-sm">
    <div class="card-header bg-success text-white">
        <h5 class="mb-0">Написать ответ</h5>
    </div>
    <div class="card-body">
        <form action="${contextPath!''}/post/create" method="post" enctype="multipart/form-data">
            <input type="hidden" name="topicId" value="${topic.id}">

            <div class="mb-3">
                <label for="postText" class="form-label">Ваш ответ</label>
                <textarea name="postText"
                          id="postText"
                          class="form-control"
                          rows="6"
                          placeholder="Напишите что-нибудь полезное..."
                          required></textarea>
                <div class="form-text text-muted mt-1">


            <div class="mb-3">
                <label for="file" class="form-label">Прикрепить файлы</label>
                <input type="file" name="file" id="file" class="form-control" multiple
                       accept="image/*,.pdf,.zip,.doc,.docx">
                <div class="form-text text-muted mt-1">
                    До 10 МБ каждый. Можно несколько.
                </div>
            </div>

            <div class="d-flex justify-content-end">
                <button type="submit" class="btn btn-success btn-lg">
                    Отправить ответ
                </button>
            </div>
        </form>
    </div>
</div>
<#else>
    <div class="alert alert-info text-center mt-4">
        <a href="${contextPath!''}/auth/login" class="alert-link">Войдите</a>, чтобы ответить
    </div>
</#if>

<script>
    document.addEventListener('click', function(e) {
        const btn = e.target.closest('button[data-reaction]');
        if (!btn) return;

        const postId = btn.dataset.postId;
        const reaction = btn.dataset.reaction;
        const container = btn.closest('.list-group-item');

        fetch('${contextPath!''}/post/' + postId + '/reaction', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'reaction=' + reaction
        })
            .then(r => r.json())
            .then(data => {
                container.querySelector('.like-count').textContent = data.likes;
                container.querySelector('.dislike-count').textContent = data.dislikes;
            })
            .catch(err => console.error('Ошибка реакции:', err));
    });
</script>

<#include "footer.ftlh">