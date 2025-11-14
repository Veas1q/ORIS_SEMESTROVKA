<footer class="bg-dark text-white text-center py-3 mt-5">
    <div class="container">
        <p class="mb-0">
            © 2025 ITIS Forum |
            Привет,
            <#if user??>
                <strong>${user.login}</strong>
            <#else>
                <strong>Гость</strong>
            </#if>
        </p>
    </div>
</footer>