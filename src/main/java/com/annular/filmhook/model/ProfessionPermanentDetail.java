package com.annular.filmhook.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "profession_permanent_detail")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProfessionPermanentDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "profession_permanent_id")
	private Integer professionPermanentId;

	@Column(name = "profession_name")
	private String professionName;

	@ElementCollection
	@Column(name = "sub_profession_name")
	private List<String> subProfessionName;

	@ManyToOne
	@JoinColumn(name = "platform_id", nullable = false)
	private PlatformPermanentDetail platformPermanentDetail;
}
