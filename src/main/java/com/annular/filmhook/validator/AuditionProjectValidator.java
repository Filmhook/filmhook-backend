package com.annular.filmhook.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.annular.filmhook.util.CustomValidator;
import com.annular.filmhook.webmodel.AuditionNewProjectWebModel;
import com.annular.filmhook.webmodel.AuditionNewTeamNeedWebModel;

@Component
public class AuditionProjectValidator implements Validator {

    private static final String BAD_REQUEST_ERROR_CD = "400";

    @Override
    public boolean supports(Class<?> clazz) {
        return AuditionNewProjectWebModel.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AuditionNewProjectWebModel projectDto = (AuditionNewProjectWebModel) target;

        // Validate main project fields
        if (CustomValidator.isEmpty(projectDto.getProductionCompanyName())) {
            errors.rejectValue("productionCompanyName", BAD_REQUEST_ERROR_CD, "Production company name is required");
        }
        if (CustomValidator.isEmpty(projectDto.getProjectTitle())) {
            errors.rejectValue("projectTitle", BAD_REQUEST_ERROR_CD, "Project title is required");
        }
        if (CustomValidator.isEmpty(projectDto.getCountry())) {
            errors.rejectValue("country", BAD_REQUEST_ERROR_CD, "Country is required");
        }
        if (projectDto.getCompanyId() == null) {
            errors.rejectValue("companyId", BAD_REQUEST_ERROR_CD, "Company ID is required");
        }

        // Validate nested team needs
        if (projectDto.getTeamNeeds() != null) {
            for (int i = 0; i < projectDto.getTeamNeeds().size(); i++) {
                AuditionNewTeamNeedWebModel teamNeed = projectDto.getTeamNeeds().get(i);

                if (CustomValidator.isEmpty(teamNeed.getRole())) {
                    errors.rejectValue("teamNeeds[" + i + "].role", BAD_REQUEST_ERROR_CD, "Role is required");
                }
                if (teamNeed.getSubProfessionId() == null) {
                    errors.rejectValue("teamNeeds[" + i + "].subProfessionId", BAD_REQUEST_ERROR_CD,
                            "Sub Profession ID is required");
                }
            }
        }
    }
}
