package com.annular.filmhook.service;

import com.annular.filmhook.webmodel.CountryWebModel;
import com.annular.filmhook.webmodel.IndustryWebModel;
import com.annular.filmhook.webmodel.PlatformWebModel;

import java.util.List;

public interface MasterDataService {

    List<CountryWebModel> getAllCountries();

    List<IndustryWebModel> getAllIndustries();

    List<PlatformWebModel> getAllPlatforms();
}
