package com.annular.filmhook.webmodel;

import com.annular.filmhook.model.FileStatus;
import com.annular.filmhook.model.MediaFileCategory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileInputWebModel {

	// For save purpose
	private Integer userId;
	private MediaFileCategory category;
	private Integer categoryRefId;
	private List<MultipartFile> files;
	private String description;

	// For read purpose
	private String fileId;
	private String fileType;
	private String filePath;
	private String type;
	private FileStatus fileStatus;
	
	

}
