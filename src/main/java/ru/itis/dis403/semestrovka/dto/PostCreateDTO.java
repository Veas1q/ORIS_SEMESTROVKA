package ru.itis.dis403.semestrovka.dto;

public class PostCreateDTO {
    private Long topicId;
    private String text;

    public PostCreateDTO() {
    }

    public PostCreateDTO(Long topicId, String text) {
        this.topicId = topicId;
        this.text = text;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
