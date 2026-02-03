package com.annular.filmhook.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "visit_page_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitPageDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Integer detailId;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "media_url")
    private String mediaUrl;

    @Column(name = "status")
    private Boolean status;

    @CreationTimestamp
    @Column(name = "created_on")
    private Date createdOn;

    // MANY DETAILS → ONE VisitPage
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_page_id", referencedColumnName = "visitPageId", nullable = false)
    private VisitPage visitPage;
}
