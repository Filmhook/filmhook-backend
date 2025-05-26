package com.annular.filmhook.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "liveSubscribe")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LiveSubscribe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "live_subscribe_id")
    private Integer liveSubscribeId;


//	@ManyToOne
//    @JoinColumn(name = "live_channel_id", referencedColumnName = "live_channel_id")
//    private LiveChannel liveChannel;

    @Column(name = "liveChannelId")
    private Integer liveChannelId;

    @Column(name = "userId")
    private Integer userId;

    @Column(name = "live_subscribe_is_active")
    private Boolean liveSubscribeIsActive;

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

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

}
