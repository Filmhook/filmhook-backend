package com.annular.filmhook.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "posts")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "post_id")
    private String postId;

    @Column(name = "description")
    private String description;

    @Column(name = "promote_flag")
    private Boolean promoteFlag;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @Column(name = "likes_count", nullable = false, columnDefinition = "int default 0")
    private Integer likesCount;

    @Column(name = "comments_count", nullable = false, columnDefinition = "int default 0")
    private Integer commentsCount;

    @Column(name = "shares_count", nullable = false, columnDefinition = "int default 0")
    private Integer sharesCount;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "postId")
    @ToString.Exclude
    @JsonIgnore
    private Collection<Likes> likesCollection;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "postId")
    @ToString.Exclude
    @JsonIgnore
    private Collection<Comment> commentCollection;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "postId")
    @ToString.Exclude
    @JsonIgnore
    private Collection<Share> shareCollection;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "created_by")
    private Integer createdBy;

    @CreationTimestamp
    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "privateOrPublic")
    private Boolean privateOrPublic;

    @Column(name = "locationName")
    private String locationName;

    @Column(name = "updated_on")
    @CreationTimestamp
    private Date updatedOn;

    @Column(name = "post_link_url")
    private String postLinkUrls;

    @Column(name = "promoteStatus")
    private Boolean promoteStatus;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "postId")
    @ToString.Exclude
    @JsonIgnore
    private Collection<PostTags> postTagsCollection;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longtitude")
    private String longitude;

    @Column(name = "address")
    private String address;
    
    @Column(name = "tagUsers")
    private String tagUsers;
    



}
