package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditionJobPostCountDTO {
    private Integer companyId;
    private Integer professionId;
    private int activePostCount; // total teamNeeds in given profession
    private int jobPostCount;    // total active teamNeeds overall
}
