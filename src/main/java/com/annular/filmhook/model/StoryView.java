package com.annular.filmhook.model;



import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "story_views")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The story being viewed
    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;
    
    @ManyToOne
    @JoinColumn(name = "media_file_id", referencedColumnName = "id")
    private MediaFiles mediaFile;

    // The user who viewed the story
    @ManyToOne
    @JoinColumn(name = "viewer_id", nullable = false)
    private User viewer;
    
    @Column(nullable = false)
    private Boolean liked = false;

    @CreationTimestamp
    @Column(name = "viewed_on", nullable = false, updatable = false)
    private Date viewedOn;
    
   
}
