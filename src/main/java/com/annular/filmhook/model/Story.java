package com.annular.filmhook.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import java.util.Date;

@Entity
@Table(name = "stories")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "story_id")
    private String storyId;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "expiry_time")
    private String expiryTime;

    @Column(name = "expiry_expression")
    private String expiryExpression;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "status")
    private Boolean status;

    @ManyToOne
    @JoinColumn(name="user_id")
    @ToString.Exclude
    private User user;

    @Column(name = "created_by")
    private Integer createdBy;

    @CreationTimestamp
    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_on")
    @CreationTimestamp
    private Date updatedOn;
}
