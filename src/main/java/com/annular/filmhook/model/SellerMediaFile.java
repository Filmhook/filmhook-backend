package com.annular.filmhook.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "seller_media_files")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SellerMediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_id")
    private Integer mediaId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "seller_id")
    private SellerInfo seller;

    @Column(name = "category") // e.g. ID_PROOF, SHOP_LOGO
    private String category;

    @Column(name = "file_id")
    private String fileId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "expiry_time")
    private String expiryTime;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_on")
    @CreationTimestamp
    private Date updatedOn;

    @Column(name = "notification_count")
    private Integer notificationCount;

    @Column(name = "unverified_list")
    private Boolean unverifiedList;
}
