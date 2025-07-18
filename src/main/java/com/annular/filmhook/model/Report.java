package com.annular.filmhook.model;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reports")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String type; // e.g., "STORY"

    @Column(nullable = false)
    private Long referenceId; // e.g., storyId

    @Column(nullable = false)
    private Long reporterUserId;

    @Column(length = 500)
    private String reason;

    @Column(length = 1000)
    private String additionalComments;

    private LocalDateTime reportedOn = LocalDateTime.now();

    private Boolean resolved = false;

    private Long resolvedBy;

    private LocalDateTime resolvedOn;

}
