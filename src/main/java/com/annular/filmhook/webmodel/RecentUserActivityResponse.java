package com.annular.filmhook.webmodel;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentUserActivityResponse {
    private List<RecentUserWebModel> search;
    private List<RecentUserWebModel> chat;
}