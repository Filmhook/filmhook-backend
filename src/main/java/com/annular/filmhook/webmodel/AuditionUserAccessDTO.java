package com.annular.filmhook.webmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditionUserAccessDTO {
    private String filmHookCode;
    private String designation;
    private String accessKey;
    private String resultStatus;
    private String message;
}