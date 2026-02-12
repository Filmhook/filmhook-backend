package com.annular.filmhook.util;

import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class LastSeenService {

    public String formatLastSeen(Date lastSeen) {

        long diffMs = System.currentTimeMillis() - lastSeen.getTime();
        long diffSec = diffMs / 1000;
        long diffMin = diffSec / 60;
        long diffHr = diffMin / 60;
        long diffDays = diffHr / 24;

        if (diffSec < 60) return "Active now";
        if (diffMin < 60) return "Active " + diffMin + " minutes ago";
        if (diffHr < 24) return "Active " + diffHr + " hours ago";
        if (diffDays == 1) return "Active yesterday";
        
        return "Active " + diffDays + " days ago";
    }
}