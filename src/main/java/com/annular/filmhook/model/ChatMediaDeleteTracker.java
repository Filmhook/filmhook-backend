package com.annular.filmhook.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "chat_media_delete_tracker")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMediaDeleteTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "media_file_id")
    private Integer mediaFileId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "chat_id")
    private Integer chatId;

    @Column(name = "deleted")
    private Boolean deleted = true;
}
