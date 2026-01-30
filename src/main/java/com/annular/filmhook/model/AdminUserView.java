package com.annular.filmhook.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "admin_user_views",
    indexes = {
        @Index(name = "idx_user_category", columnList = "user_id,category")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    // unverified | approved | rejected | deleted
    @Column(nullable = false, length = 20)
    private String category;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "viewed_at", updatable = false)
    private LocalDateTime viewedAt;
}