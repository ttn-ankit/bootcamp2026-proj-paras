package org.example.ecommerce.Auditing;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class DataAudit {

    @CreatedDate
    @Column(updatable = false,nullable = false)
     LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
     LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false,nullable = false)
     String createdBy;

    @LastModifiedBy
    @Column(nullable = false)
     String updatedBy;
}
