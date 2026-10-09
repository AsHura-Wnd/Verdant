package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "discussion_questions")
public class DiscussionQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Question title is required")
    @Size(min = 5, max = 150, message = "Title must be between 5 and 150 characters")
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank(message = "Category is required")
    @Column(nullable = false, length = 60)
    private String category; // e.g. "Plant Diseases", "Pest Control", "Soil & Watering", "Indoor Gardening"

    @NotBlank(message = "Description is required")
    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotBlank(message = "Author name is required")
    @Column(nullable = false, length = 60)
    private String author;

    private String plantType; // e.g. "Monstera Deliciosa", "Rose Bush", "Tomato"

    private String imageUrl;

    private boolean resolved = false;

    private int viewsCount = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("createdAt ASC")
    private List<DiscussionReply> replies = new ArrayList<>();

    public DiscussionQuestion() {
    }

    public DiscussionQuestion(String title, String category, String description, String author, String plantType, String imageUrl) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.author = author;
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

    public void addReply(DiscussionReply reply) {
        replies.add(reply);
        reply.setQuestion(this);
    }

    public void removeReply(DiscussionReply reply) {
        replies.remove(reply);
        reply.setQuestion(null);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
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

    public List<DiscussionReply> getReplies() {
        return replies;
    }

    public void setReplies(List<DiscussionReply> replies) {
        this.replies = replies;
    }
}
