package com.example.exam.prep.model;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
public abstract class BaseEntity {

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false; // Flag to indicate if the entity is deleted

    @Column(name = "inserted_at", nullable = false, updatable = false)
    private LocalDateTime insertedAt; // Timestamp for when the entity was created

    @Column(name = "inserted_by", nullable = false, updatable = false)
    private String insertedBy; // The user who created the entity

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Timestamp for when the entity was last updated

    @Column(name = "updated_by")
    private String updatedBy; // The user who last updated the entity

    @PrePersist
    protected void onCreate() {
        insertedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

