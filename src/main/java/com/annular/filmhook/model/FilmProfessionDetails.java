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
 * Temporary data table for Profession
 */

@Entity
@Table(name = "FilmProfessionDetails")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FilmProfessionDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "profession_detail_id")
	private Integer professionDetailId;
	
	@Column(name = "profession_temporary_detail_id")
	private Integer professionTemporaryDetailId;
	
	@Column(name = "profession_name")
	private String professionName;

	@Column(name ="userId")
	private Integer userId;

}
