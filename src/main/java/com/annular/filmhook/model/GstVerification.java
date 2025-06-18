package com.annular.filmhook.model;

import javax.persistence.*;

import lombok.Data;

@Entity
@Data
public class GstVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "gst_number", length = 30)
    private String gstNumber;
    private boolean isVerifed;
}