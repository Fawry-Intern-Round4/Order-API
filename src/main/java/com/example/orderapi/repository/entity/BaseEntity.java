package com.example.orderapi.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@MappedSuperclass
public abstract class BaseEntity {
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;
}