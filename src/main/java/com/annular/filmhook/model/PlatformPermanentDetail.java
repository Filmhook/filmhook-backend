package com.annular.filmhook.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "platform_permanent_detail")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class PlatformPermanentDetail {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "platform_permanent_id")
    private Integer platformPermanentId;
    
    @Column(name = "platform_name")
    private String platformName;
    
    @OneToMany(mappedBy = "platformPermanentDetail", cascade = CascadeType.ALL)
    private List<ProfessionPermanentDetail> professionDetails;
    
    @ManyToOne
    @JoinColumn(name = "iupd_id", nullable = false)
    private IndustryUserPermanentDetails industryUserPermanentDetails;
}
