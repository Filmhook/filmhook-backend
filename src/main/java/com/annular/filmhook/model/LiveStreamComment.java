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
@Table(name = "liveStreamComment")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LiveStreamComment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "live_Stream_Comment")
	private Integer liveStreamCommentId;
	
	@Column(name = "live_channel_id")
	private Integer liveChannelId;//foreign key of liveChannelId

	@Column(name = "user_Id")
	private Integer userId;// foreign key for user table
	
	@Column(name = "live_stream_message")
	private String liveStreamMessage;
	
	@Column(name = "live_stream_is_active")
	private Boolean liveStreamCommenIsActive;
	
    @Column(name = "live_stream_created_by")
    private Integer liveStreamCommencreatedBy;

    @CreationTimestamp
    @Column(name = "live_stream_created_on")
    private Date liveStreamCommenCreatedOn;

    @Column(name = "live_stream_updated_by")
    private Integer liveStreamCommenUpdatedBy;

    @Column(name = "live_stream_updated_on")
    @CreationTimestamp
    private Date liveStreamCommenUpdatedOn;
    
    @Column(name = "liveId")
    private String liveId;
	

}
