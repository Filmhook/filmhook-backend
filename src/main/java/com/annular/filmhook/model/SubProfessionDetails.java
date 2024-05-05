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
	
	@Column(name = "userId")
	private Integer userId;

}
