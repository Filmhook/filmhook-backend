package com.annular.filmhook.webmodel;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserRowDTO {
    private Integer id;          // User ID
    private String name;         // User name
    private String reviewedOn;   // createdOn formatted
    private String status;       // APPROVED / REJECTED
}
