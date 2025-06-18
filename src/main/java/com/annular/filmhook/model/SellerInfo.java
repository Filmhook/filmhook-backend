package com.annular.filmhook.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Entity
@Table(name = "seller_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;
    private String citizenship;
    private String placeOfBirth;
    private Long idProofNumber;

    private String doorFlatNumber;
    private String streetCross;
    private String area;
    private String state;
    private Long postalCode;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_proof_image_id")
    private SellerMediaFile idProofImage;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "shop_logo_id")
    private SellerMediaFile shopLogo;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "business_info_id")
    private BusinessInfo businessInfo;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "shop_info_id")
    private ShopInfo shopInfo;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "gst_verification_id")
    private GstVerification gstVerification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;
}
