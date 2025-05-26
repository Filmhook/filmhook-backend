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
@Table(name = "industry_temporary_Details")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IndustryTemporaryDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "it_id")
    private Integer itId;

    @Column(name = "industries_name")
    private String industriesname;

    @Column(name = "platform_name")
    private String platformname;

    @Column(name = "profession_name")
    private String professionname;

    @Column(name = "sub_profession_name")
    private String subProfessionname;

    @Column(name = "user_id")
    private Integer userId;

}
