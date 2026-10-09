package com.example.repository.jdbc;

import java.time.LocalDateTime;

/**
 * Lightweight DTO representing a gardening tip retrieved via direct JDBC PreparedStatement query.
 */
public class JdbcTipSummary {

    private final Long id;
    private final String title;
    private final String category;
    private final String summary;
    private final String author;
    private final String difficultyLevel;
    private final String plantType;
    private final int likesCount;
    private final LocalDateTime createdAt;

    public JdbcTipSummary(Long id, String title, String category, String summary,
                          String author, String difficultyLevel, String plantType,
                          int likesCount, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.summary = summary;
        this.author = author;
        this.difficultyLevel = difficultyLevel;
        this.plantType = plantType;
        this.likesCount = likesCount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getSummary() {
        return summary;
    }

    public String getAuthor() {
        return author;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public String getPlantType() {
        return plantType;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
