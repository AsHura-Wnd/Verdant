package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "discussion_replies")
public class DiscussionReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Author name is required")
    @Column(nullable = false, length = 60)
    private String author;

    @NotBlank(message = "Reply content cannot be empty")
    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private boolean expertAdvice = false;

    private int upvotes = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private DiscussionQuestion question;

    public DiscussionReply() {
    }

    public DiscussionReply(String author, String content, boolean expertAdvice) {
        this.author = author;
        this.content = content;
        this.expertAdvice = expertAdvice;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isExpertAdvice() {
        return expertAdvice;
    }

    public void setExpertAdvice(boolean expertAdvice) {
        this.expertAdvice = expertAdvice;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DiscussionQuestion getQuestion() {
        return question;
    }

    public void setQuestion(DiscussionQuestion question) {
        this.question = question;
    }
}
