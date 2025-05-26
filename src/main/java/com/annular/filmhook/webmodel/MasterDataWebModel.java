package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MasterDataWebModel {

    private CountryWebModel country;

    private IndustryWebModel industry;

    private PlatformWebModel platform;

}
