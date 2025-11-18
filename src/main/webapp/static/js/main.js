document.addEventListener("DOMContentLoaded", function () {
    const contextPath = document.body.getAttribute("data-context-path") || "";

    document.querySelectorAll(".reaction-btn").forEach(btn => {
        btn.addEventListener("click", async function () {
            const postId = this.dataset.postId;
            const reaction = this.dataset.reaction;

            try {
                const resp = await fetch(`${contextPath}/api/reaction`, {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `postId=${postId}&reaction=${reaction}`
                });
                const data = await resp.json();

                if (data.success) {
                    document.querySelector(`[data-post-id="${postId}"][data-reaction="LIKE"] .like-count`).textContent = data.likes;
                    document.querySelector(`[data-post-id="${postId}"][data-reaction="DISLIKE"] .dislike-count`).textContent = data.dislikes;
                } else {
                    alert(data.message || "Ошибка");
                }
            } catch (e) {
                console.error(e);
            }
        });
    });
});