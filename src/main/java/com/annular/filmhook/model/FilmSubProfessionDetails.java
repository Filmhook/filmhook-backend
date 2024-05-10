package com.annular.filmhook.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Temporary data table for FilmSubProfession
 */

@Entity
@Table(name = "film_sub_profession_details")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FilmSubProfessionDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sub_profession_detail_id")
	private Integer subProfessionDetailId;

	@Column(name = "sub_Profession_name")
	private String subProfessionName;
	
	@Column(name = "userId")
	private Integer userId;

	@Column(name = "industry_temporary_detail_id")
	private Integer industryTemporaryDetailId;

}
