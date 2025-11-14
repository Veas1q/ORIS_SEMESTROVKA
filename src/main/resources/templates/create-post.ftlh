<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Новый ответ — ${topic.title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<#include "header.ftlh">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow">
                <div class="card-header bg-success text-white">
                    <h4 class="mb-0">Ответить в топик: <strong>${topic.title}</strong></h4>
                </div>
                <div class="card-body">
                    <form action="/post/create" method="post">
                        <input type="hidden" name="topicId" value="${topic.id}">

                        <div class="mb-3">
                            <label for="postText" class="form-label">Ваш ответ</label>
                            <textarea name="postText" id="postText" class="form-control" rows="6"
                                      placeholder="Напишите что-нибудь полезное..." required
                                      minlength="10" maxlength="5000"></textarea>
                            <div class="form-text">Минимум 10