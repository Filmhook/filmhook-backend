package com.annular.filmhook.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

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

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "status")
    private Boolean status;

    @ManyToOne
    @JoinColumn(name="user_id")
    @ToString.Exclude
    private User user;
    
	@Column(name = "file_id")
	private String fileId;
	
	@Column(name = "category")
	private String category;

	@Column(name = "category_ref_id") // for all referred table's[Post,Story] primary key
	private Integer categoryRefId;
	
	@Column(name = "file_name")
	private String fileName;
	
	@Column(name = "file_size")
	private Long fileSize;

	@Column(name = "file_type")
	private String fileType;

	@Column(name = "file_path")
	private String filePath;

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
