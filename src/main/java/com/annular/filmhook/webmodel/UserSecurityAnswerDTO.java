package com.annular.filmhook.webmodel;
import com.annular.filmhook.model.UserSecurityQuestion;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class UserSecurityAnswerDTO extends BasicFeildsDTO {

    private Integer id;
    private Integer userId;  
    private UserSecurityQuestion question;
    private String answer;

}
