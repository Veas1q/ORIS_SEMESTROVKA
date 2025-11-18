<#include "header.ftlh">
<#assign pageTitle = "Админка — Пользователи" />

<div class="container mt-5">
    <h1>Управление пользователями</h1>
    <p>Привет, <strong>${currentUser.login}</strong>! Ваша роль:
        <span class="badge bg-primary">${currentUser.role}</span>
    </p>

    <#if users?? && (users?size > 0)>
        <div class="table-responsive">
            <table class="table table-striped align-middle">
                <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Пользователь</th>
                    <th>Email</th>
                    <th>Роль</th>
                    <th>Статус</th>
                    <th>Действия</th>
                </tr>
                </thead>
                <tbody>
                <#list users as u>
                    <tr class="<#if u.isBanned>table-danger<#elseif u.role == 'ADMIN'>table-info</#if>">
                        <td>${u.id}</td>
                        <td>
                            <strong>${u.login}</strong>
                        </td>
                        <td>${u.email}</td>
                        <td>
                            <span class="badge bg-${(u.role == 'ADMIN')?then('danger', (u.role == 'MODERATOR')?then('warning', 'secondary'))}">
                                ${u.role}
                            </span>
                        </td>
                        <td>
                            <#if u.isBanned>
                                <span class="text-danger">
                                    Забанен
                                    <#if u.bannedUntil??>
                                        <br><small>до ${u.bannedUntil?replace('T', ' ')?substring(0, 16)}</small>
                                    </#if>
                                    <#if u.banReason?? && u.banReason?has_content>
                                        <br><small class="text-muted">Причина: ${u.banReason}</small>
                                    </#if>
                                </span>
                            <#else>
                                Активен
                            </#if>
                        </td>
                        <td>
                            <#if u.id != currentUser.id && u.role != 'ADMIN'>
                                <#if !u.isBanned>
                                    <button type="button" class="btn btn-sm btn-danger" data-bs-toggle="modal"
                                            data-bs-target="#banModal${u.id}">
                                        Забанить
                                    </button>
                                <#else>
                                    <form method="post" class="d-inline">
                                        <input type="hidden" name="userId" value="${u.id}">
                                        <input type="hidden" name="action" value="unban">
                                        <button type="submit" class="btn btn-sm btn-success">Разбанить</button>
                                    </form>
                                </#if>

                                <#if currentUser.role == 'ADMIN'>
                                    <#if u.role != 'MODERATOR'>
                                        <form method="post" class="d-inline ms-1">
                                            <input type="hidden" name="userId" value="${u.id}">
                                            <input type="hidden" name="action" value="set_moderator">
                                            <button type="submit" class="btn btn-sm btn-warning">Модератор</button>
                                        </form>
                                    <#else>
                                        <form method="post" class="d-inline ms-1">
                                            <input type="hidden" name="userId" value="${u.id}">
                                            <input type="hidden" name="action" value="set_user">
                                            <button type="submit" class="btn btn-sm btn-secondary">Пользователь</button>
                                        </form>
                                    </#if>
                                </#if>
                            </#if>
                        </td>
                    </tr>

                    <!-- МОДАЛКА БАНА -->
                    <div class="modal fade" id="banModal${u.id}" tabindex="-1">
                        <div class="modal-dialog">
                            <form method="post">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title">Забанить пользователя ${u.login}</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                    </div>
                                    <div class="modal-body">
                                        <input type="hidden" name="userId" value="${u.id}">
                                        <input type="hidden" name="action" value="ban">

                                        <div class="mb-3">
                                            <label class="form-label">Причина бана</label>
                                            <textarea name="reason" class="form-control" rows="3" required></textarea>
                                        </div>

                                        <div class="mb-3">
                                            <label class="form-label">Дней бана (пусто = навсегда)</label>
                                            <input type="number" name="days" class="form-control" min="1" placeholder="7">
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена</button>
                                        <button type="submit" class="btn btn-danger">Забанить</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </#list>
                </tbody>
            </table>
        </div>
    <#else>
        <div class="alert alert-info">Пользователей нет.</div>
    </#if>
</div>

<#include "footer.ftlh">