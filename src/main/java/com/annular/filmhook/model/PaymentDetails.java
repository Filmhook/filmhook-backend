package com.annular.filmhook.model;

import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "payment_detailss")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "txnid")
    private String txnid;

    @Column(name = "amount")
    private String amount;

    @Column(name = "productinfo")
    private String productinfo;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "email")
    private String email;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "created_by")
    private Integer createdBy;

    @CreationTimestamp
    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_on")
    @CreationTimestamp
    private Date updatedOn;
    
    @Column(name = "status")
    private Boolean status;
    
    @Column(name = "payment_hash")
    private String paymentHash;

}
