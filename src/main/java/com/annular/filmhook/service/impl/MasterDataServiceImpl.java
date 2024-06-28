package com.annular.filmhook.service.impl;

import com.annular.filmhook.model.Country;
import com.annular.filmhook.model.Industry;
import com.annular.filmhook.model.Platform;
import com.annular.filmhook.repository.CountryRepository;
import com.annular.filmhook.repository.IndustryRepository;
import com.annular.filmhook.repository.PlatformRepository;
import com.annular.filmhook.service.MasterDataService;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.CountryWebModel;
import com.annular.filmhook.webmodel.IndustryWebModel;

import com.annular.filmhook.webmodel.PlatformWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
public class MasterDataServiceImpl implements MasterDataService {

    private static final Logger logger = LoggerFactory.getLogger(MasterDataService.class);

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    IndustryRepository industryRepository;

    @Autowired
    PlatformRepository platformRepository;

    @Autowired
    S3Util s3Util;

    @Override
    public List<CountryWebModel> getAllCountries() {
        return this.transformCountryData(countryRepository.findAll());
    }

    private List<CountryWebModel> transformCountryData(List<Country> countryList) {
        List<CountryWebModel> outputList = new ArrayList<>();
        try {
            countryList.stream().filter(Objects::nonNull).forEach(country -> {
                CountryWebModel countryWebModel = CountryWebModel.builder()
                        .id(country.getId())
                        .code(country.getCode())
                        .name(country.getName())
                        .description(country.getDescription())
                        //.logo(country.getLogo() != null ? Base64.getEncoder().encode(country.getLogo()) : null)
                        .filePath(!Utility.isNullOrBlankWithTrim(country.getFilePath()) ? s3Util.generateS3FilePath(country.getFilePath()) : "")
                        .build();
                outputList.add(countryWebModel);
            });
        } catch (Exception e) {
            logger.error("Error at transformCountryData() -> [{}]", e.getMessage());
            e.printStackTrace();
        }
        return outputList;
    }

    @Override
    public List<IndustryWebModel> getAllIndustries() {
        return this.transformIndustryData(industryRepository.findAll());
    }

    public List<IndustryWebModel> transformIndustryData(List<Industry> industryList) {
        List<IndustryWebModel> outputList = new ArrayList<>();
        try {
            industryList.stream().filter(Objects::nonNull).forEach(industry -> {
                IndustryWebModel industryWebModel = IndustryWebModel.builder()
                        .id(industry.getIndustryId())
                        .industryName(industry.getIndustryName())
                        .status(industry.getStatus())
                        .stateCode(industry.getStateCode())
                        .countryId(industry.getCountry() != null ? industry.getCountry().getId() : null)
                        //.image(industry.getImage() != null ? Base64.getEncoder().encode(industry.getImage()) : null)
                        .iconFilePath(!Utility.isNullOrBlankWithTrim(industry.getFilePath()) ? s3Util.generateS3FilePath(industry.getFilePath()) : "")
                        .build();
                outputList.add(industryWebModel);
            });
        } catch (Exception e) {
            logger.error("Error at transformIndustryData() -> [{}]", e.getMessage());
            e.printStackTrace();
        }
        return outputList;
    }

    @Override
    public List<PlatformWebModel> getAllPlatforms() {
        return this.transformPlatformData(platformRepository.findAll());
    }

    public List<PlatformWebModel> transformPlatformData(List<Platform> platformList) {
        List<PlatformWebModel> outputList = new ArrayList<>();
        try {
            platformList.stream().filter(Objects::nonNull).forEach(platform -> {
                PlatformWebModel platformWebModel = PlatformWebModel.builder()
                        .id(platform.getPlatformId())
                        .platformName(platform.getPlatformName())
                        .status(platform.getStatus())
                        //.image(platform.getImage() != null ? Base64.getEncoder().encode(platform.getImage()) : null)
                        .iconFilePath(!Utility.isNullOrBlankWithTrim(platform.getFilePath()) ? s3Util.generateS3FilePath(platform.getFilePath()) : "")
                        .build();
                outputList.add(platformWebModel);
            });
        } catch (Exception e) {
            logger.error("Error at transformPlatformData() -> [{}]", e.getMessage());
            e.printStackTrace();
        }
        return outputList;
    }

}
