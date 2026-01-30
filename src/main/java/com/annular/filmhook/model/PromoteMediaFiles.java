package com.annular.filmhook.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "promote_media_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoteMediaFiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Which promotion this selection belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promote_id")
    private PromoteAd promote;

    // Which media file is selected
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_file_id")
    private MediaFiles mediaFile;

    
    // Whether this media is selected
    @Column(name = "selected")
    private Boolean selected;
}