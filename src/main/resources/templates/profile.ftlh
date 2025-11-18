<#--<#include "header.ftlh">-->
<#--<#assign pageTitle = "Профиль - Formach" />-->

<#--<div class="container mt-5">-->
<#--    <#if error??>-->
<#--        <div class="alert alert-danger">${error}</div>-->
<#--    </#if>-->

<#--    <div class="row">-->
<#--        <!-- ЛЕВАЯ КОЛОНКА &ndash;&gt;-->
<#--        <div class="col-md-4">-->
<#--            <div class="card">-->
<#--                <div class="card-body text-center">-->
<#--                    <#if user.avatarUrl??>-->
<#--                        <img src="${contextPath!''}${user.avatarUrl}"-->
<#--                             alt="Аватар"-->
<#--                             class="rounded-circle mb-3"-->
<#--                             width="150" height="150"-->
<#--                             style="object-fit: cover;">-->
<#--                    <#else>-->
<#--                        <div class="bg-secondary rounded-circle d-block mx-auto mb-3"-->
<#--                             style="width:150px;height:150px;"></div>-->
<#--                    </#if>-->
<#--                    <h4>${user.login}</h4>-->

<#--                    <!-- РОЛЬ &ndash;&gt;-->
<#--                    <#if user.role == 'ADMIN'>-->
<#--                        <span class="badge bg-danger mb-2">Администратор</span>-->
<#--                    <#elseif user.role == 'MODERATOR'>-->
<#--                        <span class="badge bg-warning text-dark mb-2">Модератор</span>-->
<#--                    <#else>-->
<#--                        <span class="badge bg-secondary mb-2">Пользователь</span>-->
<#--                    </#if>-->

<#--                    <p class="text-muted">${user.email}</p>-->
<#--                    <p class="text-muted">${user.phoneNumber!"Не указан"}</p>-->
<#--                </div>-->
<#--            </div>-->
<#--        </div>-->

<#--        <!-- ПРАВАЯ КОЛОНКА &ndash;&gt;-->
<#--        <div class="col-md-8">-->
<#--            <!-- СТАТИСТИКА &ndash;&gt;-->
<#--            <div class="card">-->
<#--                <div class="card-header">-->
<#--                    <h5>Статистика</h5>-->
<#--                </div>-->
<#--                <div class="card-body">-->
<#--                    <div class="row text-center">-->
<#--                        <div class="col-6">-->
<#--                            <h3>${topicCount}</h3>-->
<#--                            <p class="text-muted">Тем</p>-->
<#--                        </div>-->
<#--                        <div class="col-6">-->
<#--                            <h3>${postCount}</h3>-->
<#--                            <p class="text-muted">Постов</p>-->
<#--                        </div>-->
<#--                    </div>-->
<#--                </div>-->
<#--            </div>-->

<#--            <!-- РЕДАКТИРОВАНИЕ &ndash;&gt;-->
<#--            <div class="card mt-4">-->
<#--                <div class="card-header">-->
<#--                    <h5>Редактировать профиль</h5>-->
<#--                </div>-->
<#--                <div class="card-body">-->
<#--                    <form method="post" enctype="multipart/form-data">-->
<#--                        <div class="mb-3">-->
<#--                            <label class="form-label">Имя</label>-->
<#--                            <input type="text" name="firstName" class="form-control" value="${user.firstName!''}">-->
<#--                        </div>-->
<#--                        <div class="mb-3">-->
<#--                            <label class="form-label">Фамилия</label>-->
<#--                            <input type="text" name="lastName" class="form-control" value="${user.lastName!''}">-->
<#--                        </div>-->
<#--                        <div class="mb-3">-->
<#--                            <label class="form-label">Email</label>-->
<#--                            <input type="email" name="email" class="form-control" value="${user.email!''}" required>-->
<#--                        </div>-->
<#--                        <div class="mb-3">-->
<#--                            <label class="form-label">Телефон</label>-->
<#--                            <input type="tel" name="phone" class="form-control" value="${user.phoneNumber!''}">-->
<#--                        </div>-->
<#--                        <div class="mb-3">-->
<#--                            <label class="form-label">Дата рождения</label>-->
<#--                            <input type="date" name="birthDate" class="form-control"-->
<#--                                   value="${user.birthDate!''}">-->
<#--                        </div>-->
<#--                        <div class="mb-3">-->
<#--                            <label class="form-label">Аватарка</label>-->
<#--                            <input type="file" name="avatar" class="form-control" accept="image/*">-->
<#--                            <div class="form-text">JPG, PNG, GIF. Макс 2 МБ.</div>-->
<#--                        </div>-->
<#--                        <button type="submit" class="btn btn-primary">Сохранить</button>-->
<#--                    </form>-->
<#--                </div>-->
<#--            </div>-->
<#--        </div>-->
<#--    </div>-->
<#--</div>-->

<#--<#include "footer.ftlh">-->