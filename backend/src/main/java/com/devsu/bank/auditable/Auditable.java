package com.devsu.bank.auditable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime deletedAt;

    @CreatedBy
    @JsonIgnore
    @Column(name = "created_by", nullable = false, updatable = false, columnDefinition = "VARCHAR(100)")
    private String createdBy;

    @LastModifiedBy
    @JsonIgnore
    @Column(name = "updated_by", columnDefinition = "VARCHAR(100)")
    private String updatedBy;

    @JsonIgnore
    @Column(name = "deleted_by", columnDefinition = "VARCHAR(100)")
    private String deletedBy;

    @JsonIgnore
    @Column(name = "is_deleted", nullable = false, columnDefinition = "BIT DEFAULT 0")
    @Builder.Default
    private boolean isDeleted = false;

    /**
     * Marks the entity as logically deleted
     *
     * @param deletedBy String of the user performing the deletion
     */
    public void softDelete(String deletedBy) {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}

