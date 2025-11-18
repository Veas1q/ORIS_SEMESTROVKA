<#include "header.ftlh">
<#assign pageTitle = topic.title />

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
        <div class="list-group-item p-4">
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

                    <div class="reaction-container d-flex gap-3">
                        <button class="btn btn-outline-success reaction-btn
                            <#if userReactionMap?? && userReactionMap[post.id?c]?? && userReactionMap[post.id?c] == 'LIKE'>btn-success</#if>"
                                data-reaction="LIKE" data-post-id="${post.id}">
                            <i class="bi bi-hand-thumbs-up"></i> <span class="like-count">${post.likes!0}</span>
                        </button>
                        <button class="btn btn-outline-danger reaction-btn
                            <#if userReactionMap?? && userReactionMap[post.id?c]?? && userReactionMap[post.id?c] == 'DISLIKE'>btn-danger</#if>"
                                data-reaction="DISLIKE" data-post-id="${post.id}">
                            <i class="bi bi-hand-thumbs-down"></i> <span class="dislike-count">${post.dislikes!0}</span>
                        </button>
                    </div>
                </div>
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

<!-- ВСТРОЕННЫЙ РАБОЧИЙ СКРИПТ — ЛАЙКИ РАБОТАЮТ СРАЗУ -->
<script>
    const contextPath = document.body.getAttribute("data-context-path") || "";

    document.querySelectorAll(".reaction-btn").forEach(btn => {
        btn.addEventListener("click", async function () {
            const postId = this.dataset.postId;
            const reaction = this.dataset.reaction;

            try {
                const resp = await fetch(contextPath + "/api/reaction", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `postId=\${postId}&reaction=\${reaction}`
                });
                const data = await resp.json();

                if (data.success) {
                    document.querySelector(`[data-post-id="\${postId}"][data-reaction="LIKE"] .like-count`).textContent = data.likes;
                    document.querySelector(`[data-post-id="\${postId}"][data-reaction="DISLIKE"] .dislike-count`).textContent = data.dislikes;

                    // Подсвечиваем текущую реакцию
                    document.querySelectorAll(`[data-post-id="\${postId}"]`).forEach(b => {
                        b.classList.remove("btn-success", "btn-danger");
                    });
                    if (data.userReaction === "LIKE") {
                        document.querySelector(`[data-post-id="\${postId}"][data-reaction="LIKE"]`).classList.add("btn-success");
                    } else if (data.userReaction === "DISLIKE") {
                        document.querySelector(`[data-post-id="\${postId}"][data-reaction="DISLIKE"]`).classList.add("btn-danger");
                    }
                } else {
                    alert(data.message || "Ошибка");
                }
            } catch (e) {
                console.error(e);
                alert("Не удалось отправить реакцию");
            }
        });
    });
</script>

<#include "footer.ftlh">