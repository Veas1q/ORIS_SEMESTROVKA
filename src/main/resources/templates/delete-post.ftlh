<#include "header.ftlh">
<#assign pageTitle = "Удалить пост?" />

<div class="container">
    <div class="delete-box card shadow border-danger">
        <div class="card-header bg-danger text-white text-center">
            <h3>Удалить пост навсегда?</h3>
        </div>
        <div class="card-body">
            <p class="text-center text-danger fw-bold">Это действие нельзя отменить!</p>

            <div class="post-preview">
                <p><strong>Автор:</strong> ${postAuthor.login}</p>
                <p><strong>Создан:</strong> ${post.createdAt!"—"}</p>
                <hr>
                <p>${post.postText}</p>
            </div>

            <div class="text-center">
                <form action="${contextPath}/post/${post.id}/delete" method="post" class="d-inline">
                    <button type="submit" class="btn btn-danger btn-lg px-5">Да, удалить навсегда</button>
                </form>
                <a href="${contextPath}/topic/${post.topicId}" class="btn btn-secondary btn-lg px-5 ms-3">Нет, оставить</a>
            </div>
        </div>
    </div>
</div>

<#include "footer.ftlh">