package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetailRequest {

    private boolean industries;
    private boolean platforms;
    private boolean professions;
    private boolean subProfessions;

}
