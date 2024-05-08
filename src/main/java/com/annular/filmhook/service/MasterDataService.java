package com.annular.filmhook.service;

import com.annular.filmhook.webmodel.MasterDataWebModel;

import java.util.List;

public interface MasterDataService {

    List<MasterDataWebModel> getAllCountries();

    List<MasterDataWebModel> getAllIndustries();

    List<MasterDataWebModel> getAllPlatforms();
}
