package com.annular.filmhook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "SubProfessionDetails")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SubProfessionDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sub_profession_detail_id")
	private Integer subProfessionDetailId;
	
	@Column(name = "industry_temporary_detail_id")
	private Integer integerTemporaryDetailId;
	
	@Column(name = "sub_Profession_name")
	private String subProfessionName;

}
