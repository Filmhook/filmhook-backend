package com.annular.filmhook.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "watch_later")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchLater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Posts post;

    // 👇 Maintain active/inactive instead of delete
    @Column(name = "status", nullable = false, columnDefinition = "boolean default true")
    private Boolean status = true;

    @CreationTimestamp
    @Column(name = "created_on")
    private Date createdOn;

    @CreationTimestamp
    @Column(name = "updated_on")
    private Date updatedOn;
}

