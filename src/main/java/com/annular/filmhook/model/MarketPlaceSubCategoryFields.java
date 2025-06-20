package com.annular.filmhook.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.annular.filmhook.util.StringListConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "marketplace_category_fields")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketPlaceSubCategoryFields {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;

private String fieldKey;  
//private String label;
private String type; 
private boolean required;
private String section;


@ElementCollection
private List<String> options;

@ManyToOne
@JoinColumn(name = "subcategory_id", nullable = false)
@JsonIgnore
private MarketPlaceSubCategories subCategories;

}





