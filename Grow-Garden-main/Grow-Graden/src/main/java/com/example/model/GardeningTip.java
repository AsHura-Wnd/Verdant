package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "gardening_tips")
public class GardeningTip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank(message = "Category is required")
    @Column(nullable = false, length = 50)
    private String category;

    @NotBlank(message = "Summary is required")
    @Size(max = 300, message = "Summary cannot exceed 300 characters")
    @Column(length = 300)
    private String summary;

    @NotBlank(message = "Content is required")
    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @NotBlank(message = "Author name is required")
    @Column(nullable = false, length = 60)
    private String author;

    private String difficultyLevel = "Beginner"; // Beginner, Intermediate, Advanced

    private String plantType; // e.g. Vegetables, Indoor Plants, Succulents, Flowers

    private String imageUrl;

    private int likesCount = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public GardeningTip() {
    }

    public GardeningTip(String title, String category, String summary, String content, String author,
                        String difficultyLevel, String plantType, String imageUrl) {
        this.title = title;
        this.category = category;
        this.summary = summary;
        this.content = content;
        this.author = author;
        this.difficultyLevel = difficultyLevel;
        this.plantType = plantType;
        this.imageUrl = imageUrl;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getPlantType() {
        return plantType;
    }

    public void setPlantType(String plantType) {
        this.plantType = plantType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
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
}
