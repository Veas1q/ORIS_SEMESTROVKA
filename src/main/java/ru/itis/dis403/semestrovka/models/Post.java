package ru.itis.dis403.semestrovka.models;

import java.time.LocalDateTime;

public class Post {
    private Long id;
    private Long userId;
    private Long topicId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String postText;
    private Boolean isFirstPost;
    private Boolean isPinnedInTopic = false;
    private Long pinnedByUserId;
    private LocalDateTime pinnedAt;

    public Long getPinnedByUserId() {
        return pinnedByUserId;
    }

    public void setPinnedByUserId(Long pinnedByUserId) {
        this.pinnedByUserId = pinnedByUserId;
    }

    public LocalDateTime getPinnedAt() {
        return pinnedAt;
    }

    public void setPinnedAt(LocalDateTime pinnedAt) {
        this.pinnedAt = pinnedAt;
    }

    public Boolean getPinnedInTopic() {
        return isPinnedInTopic;
    }

    public void setPinnedInTopic(Boolean pinnedInTopic) {
        isPinnedInTopic = pinnedInTopic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public Boolean getFirstPost() {
        return isFirstPost;
    }

    public void setFirstPost(Boolean firstPost) {
        isFirstPost = firstPost;
    }

}
