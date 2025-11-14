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
                    <#if user?? && (user.id == post.userId || user.role == 'ADMIN')>
                        <div class="d-flex gap-1 mt-1">
                            <a href="${contextPath!''}/post/${post.id}/edit"
                               class="btn btn-sm btn-outline-primary">Edit</a>
                            <a href="${contextPath!''}/post/${post.id}/delete"
                               onclick="return confirm('Удалить пост?')"
                               class="btn btn-sm btn-outline-danger">Delete</a>
                        </div>
                    </#if>
                </div>
            </div>
        </div>
    </#list>
</div>

<#if user??>
    <div class="card">
        <div class="card-body">
            <strong class="d-block mb-2">Написать ответ:</strong>
            <form action="${contextPath!''}/post/create" method="post">
                <input type="hidden" name="topicId" value="${topic.id}">
                <textarea name="postText" class="form-control" rows="3" required placeholder="Ваш ответ..."></textarea>
                <button type="submit" class="btn btn-primary mt-2">Отправить</button>
            </form>
        </div>
    </div>
<#else>
    <div class="alert alert-info text-center">
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