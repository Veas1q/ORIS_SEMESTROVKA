<td>
    <img src="${contextPath!''}${(u.avatarUrl??)?then(u.avatarUrl, '/img/default-avatar.png')}"
         width="30" height="30" class="rounded-circle me-2">
    ${u.login}
</td>

<td>
    <#if u.isBanned>
        <span class="text-danger">
            Забанен
            <#if u.bannedUntil??>
                <br><small>до ${u.bannedUntil?string("dd.MM.yyyy HH:mm")}</small>
            </#if>
            <#if u.banReason??>
                <br><small class="text-muted">Причина: ${u.banReason}</small>
            </#if>
        </span>
    <#else>
        Активен
    </#if>
</td>