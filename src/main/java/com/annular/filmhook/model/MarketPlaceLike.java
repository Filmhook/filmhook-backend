package com.annular.filmhook.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "marketPlaceLike")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MarketPlaceLike {
	

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "market_place_like_id")
	    private Integer marketPlaceLikeId;

        @Column(name = "marketPlace_Id")
	    private Integer marketPlaceId; // primary key for post table

	    @Column(name = "market_place_liked_by")
	    private Integer marketPlacelikedBy; // primary key for user table

	    @Column(name = "status")
	    private Boolean status;

	    @Column(name = "created_by")
	    private Integer createdBy;

	    @CreationTimestamp
	    @Column(name = "created_on")
	    private Date createdOn;

	    @Column(name = "updated_by")
	    private Integer updatedBy;

	    @Column(name = "updated_on")
	    @UpdateTimestamp
	    private Date updatedOn;

	

}
