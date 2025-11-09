package ru.itis.dis403.semestrovka.dto;

public class TopicCreateDTO {
    private String title;
    private Long categoryId;
    private String firstPostText;

    public TopicCreateDTO() {
    }

    public TopicCreateDTO(String title, Long categoryId, String firstPostText) {
        this.title = title;
        this.categoryId = categoryId;
        this.firstPostText = firstPostText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getFirstPostText() {
        return firstPostText;
    }

    public void setFirstPostText(String firstPostText) {
        this.firstPostText = firstPostText;
    }
}
