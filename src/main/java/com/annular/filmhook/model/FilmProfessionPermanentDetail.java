package com.annular.filmhook.model;

import java.util.List;

import javax.persistence.CascadeType;

//import java.util.List;

import javax.persistence.Column;
//import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Permanent data with user id table for Profession
 */

@Entity
@Table(name = "film_profession_permanent_detail")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FilmProfessionPermanentDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "profession_permanent_id")
	private Integer professionPermanentId;

	@Column(name = "profession_name")
	private String professionName;
	
	@Column(name = "ppd_profession_id")
	private Integer ppdProfessionId;

	/*@ElementCollection
	@Column(name = "sub_profession_name")
	private List<String> subProfessionName;*/

	@ManyToOne
	@JoinColumn(name = "industry_permanent_id", nullable = false)
	@ToString.Exclude
	@JsonIgnore
	private IndustryUserPermanentDetails industryUserPermanentDetails;

	@ManyToOne
	@JoinColumn(name = "platform_permanent_id", nullable = false)
	@ToString.Exclude
	@JsonIgnore
	private PlatformPermanentDetail platformPermanentDetail;

	@ManyToOne
	@JoinColumn(name = "profession_id", nullable = false)
	@ToString.Exclude
	@JsonIgnore
	private FilmProfession filmProfession;

	@Column(name = "userId")
	private Integer userId;

	@OneToMany(mappedBy = "filmProfessionPermanentDetail", cascade = CascadeType.ALL)
	@JsonIgnore
	@ToString.Exclude
	private List<FilmSubProfessionPermanentDetail> filmSubProfessionPermanentDetails;
}
