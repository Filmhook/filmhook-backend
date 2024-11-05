package com.annular.filmhook.webmodel;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ShootingLocationChatWebModel {
	

    private Integer shootingLocationChatId;
    private Integer shootingLocationSenderId;
    private Integer shootingLocationReceiverId;
    private String message;
    private Boolean shootingLocationIsActive;
    private Integer shootingLocationCreatedBy;
    private Integer shootingLocationUpdatedBy;
    private Date shootingLocationCreatedOn;
    private Date shootingLocationUpdatedOn;
    private Date timeStamp;
    List<MultipartFile> files;
    List<FileOutputWebModel> chatFiles;
    private Integer userId;
	private String shootingLocationStartTime;
	private String shootingLocationEndTime;
	private Boolean accept;
	

}
