package com.annular.filmhook.converter;

import com.annular.filmhook.model.UserSecurityAnswer;
import com.annular.filmhook.webmodel.UserSecurityAnswerDTO;

public class UserSecurityAnswerConverter {
	
	 public static UserSecurityAnswerDTO convertToDTO(UserSecurityAnswer entity) {

	        if (entity == null) {
	            return null;
	        }

	        UserSecurityAnswerDTO dto = UserSecurityAnswerDTO.builder()
	                .id(entity.getId())
	                .userId(entity.getUser() != null 
	                        ? entity.getUser().getUserId() 
	                        : null)
	                .question(entity.getQuestion())
	                .build();

	        // BasicEntity fields mapping
	        dto.setCreatedOn(entity.getCreatedOn());
	        dto.setUpdatedOn(entity.getUpdatedOn());
	        dto.setCreatedBy(entity.getCreatedBy());
	        dto.setUpdatedBy(entity.getUpdatedBy());
	        dto.setStatus(entity.getStatus());

	        return dto;
	    }
}
