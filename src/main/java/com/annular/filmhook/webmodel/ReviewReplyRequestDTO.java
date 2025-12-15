package com.annular.filmhook.webmodel;


import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReplyRequestDTO {
 private Integer reviewId;
 private Integer ownerUserId; // the caller (should be property owner)
 private String replyText;
}

