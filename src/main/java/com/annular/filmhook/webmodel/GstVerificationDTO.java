package com.annular.filmhook.webmodel;

import lombok.Data;

@Data
public class GstVerificationDTO {
    private String gstNumber;
    private boolean isVerifed;
}