package com.annular.filmhook.webmodel;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerStatusUpdateDTO {
    private Long sellerId;
    private boolean buttonStatus;
    private String activeStatus; // values: "Approved", "Pending", "Rejected"
    private String reason;
}
