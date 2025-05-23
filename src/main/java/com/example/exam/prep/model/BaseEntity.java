package com.example.exam.prep.model;

import jakarta.persistence.*;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inserted_at", updatable = false)
    private Instant insertedAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inserted_by_id", referencedColumnName = "id", updatable = false)
    @CreatedBy
    private User insertedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_id", referencedColumnName = "id")
    @LastModifiedBy
    private User updatedBy;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @PrePersist
    protected void updateInsertedAt() {
        insertedAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void updateUpdatedAt() {
        updatedAt = Instant.now();
    }
}
