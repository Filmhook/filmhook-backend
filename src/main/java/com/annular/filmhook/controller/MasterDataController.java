package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.MasterDataService;
import com.annular.filmhook.webmodel.CountryWebModel;
import com.annular.filmhook.webmodel.IndustryWebModel;
import com.annular.filmhook.webmodel.PlatformWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/masterData")
public class MasterDataController {

    private static final Logger logger = LoggerFactory.getLogger(MasterDataController.class);

    @Autowired
    MasterDataService masterDataService;

    @GetMapping("/getAllCountry")
    public Response getAllCountries() {
        try {
            List<CountryWebModel> countryList = masterDataService.getAllCountries();
            return new Response(1, "Success", countryList);
        } catch (Exception e) {
            logger.error("Error at getAllCountries() -> [{}]", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error", e.getMessage());
        }
    }

    @GetMapping("/getAllIndustry")
    public Response getAllIndustries() {
        try {
            List<IndustryWebModel> industryList = masterDataService.getAllIndustries();
            return new Response(1, "Success", industryList);
        } catch (Exception e) {
            logger.error("Error at getAllIndustries() -> [{}]", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error", e.getMessage());
        }
    }

    @GetMapping("/getAllPlatform")
    public Response getAllPlatform() {
        try {
            List<PlatformWebModel> platform = masterDataService.getAllPlatforms();
            return new Response(1, "Success", platform);
        } catch (Exception e) {
            logger.error("Error at getAllPlatform() -> [{}]", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error", e.getMessage());
        }
    }

}
