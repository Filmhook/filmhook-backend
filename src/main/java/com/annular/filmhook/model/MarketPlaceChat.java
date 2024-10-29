package com.annular.filmhook.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "marketPlaceChat")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MarketPlaceChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "marketPlaceChatId")
    private Integer marketPlaceChatId;

    @Column(name = "market_place_sender_id")
    private Integer marketPlaceSenderId;

    @Column(name = "market_place_receiver_id")
    private Integer marketPlaceReceiverId;

    @Column(name = "message")
    private String message;

    @Column(name = "market_place_is_active")
    private Boolean marketPlaceIsActive;

    @Column(name = "market_place_created_by")
    private Integer marketPlaceCreatedBy;

    @Column(name = "marke_tplace_updated_by")
    private Integer marketPlaceUpdatedBy;

    @CreationTimestamp
    @Column(name = "market_place_created_on")
    private Date marketPlaceCreatedOn;

    @CreationTimestamp
    @Column(name = "market_place_updated_on")
    private Date marketPlaceUpdatedOn;

    @Column(name = "market_type")
    private String marketType;

    @CreationTimestamp
    @Column(name = "time_stamp")
    private Date timeStamp;
    
    @Column(name = "accept")
    private Boolean accept;
}