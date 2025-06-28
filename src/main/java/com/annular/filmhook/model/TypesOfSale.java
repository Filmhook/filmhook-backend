package com.annular.filmhook.model;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
public class TypesOfSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name; // values like "Sale", "Rental", "Sale/Rental"
}
