package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gardening_projects")
public class GardenProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Project title is required")
    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    @Column(nullable = false, length = 120)
    private String title;

    @NotBlank(message = "Description is required")
    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotBlank(message = "Plant type is required")
    @Column(nullable = false, length = 80)
    private String plantType; // e.g. "Culinary Herbs", "Cherry Tomatoes", "Indoor Monsteras & Ferns"

    @NotBlank(message = "Location/Space type is required")
    @Column(nullable = false, length = 60)
    private String location; // Balcony, Backyard, Indoor Windowsill, Rooftop, Raised Beds

    @NotBlank(message = "Status is required")
    @Column(nullable = false, length = 30)
    private String status = "In Progress"; // "Planning", "In Progress", "Flourishing", "Harvested", "Completed"

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetDate;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String notes; // Care routine, soil mixture, milestones

    private String imageUrl;

    @NotBlank(message = "Gardener name is required")
    @Column(nullable = false, length = 60)
    private String gardenerName;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public GardenProject() {
    }

    public GardenProject(String title, String description, String plantType, String location,
                         String status, LocalDate startDate, LocalDate targetDate,
                         String notes, String imageUrl, String gardenerName) {
        this.title = title;
        this.description = description;
        this.plantType = plantType;
        this.location = location;
        this.status = status;
        this.startDate = startDate;
        this.targetDate = targetDate;
        this.notes = notes;
        this.imageUrl = imageUrl;
        this.gardenerName = gardenerName;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.startDate == null) {
            this.startDate = LocalDate.now();
        }
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlantType() {
        return plantType;
    }

    public void setPlantType(String plantType) {
        this.plantType = plantType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGardenerName() {
        return gardenerName;
    }

    public void setGardenerName(String gardenerName) {
        this.gardenerName = gardenerName;
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
