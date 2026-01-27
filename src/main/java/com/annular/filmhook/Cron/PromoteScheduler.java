package com.annular.filmhook.Cron;

import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.PromoteAd;
import com.annular.filmhook.model.PromoteAd.PromoteStatus;
import com.annular.filmhook.repository.PostsRepository;
import com.annular.filmhook.repository.PromoteAdRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromoteScheduler {

    private final PromoteAdRepository promoteAdRepository;
    private final PostsRepository postsRepository;

    @Scheduled(cron = "0 0 * * * *") // every 1 hour
    public void autoCompletePromotions() {

        Date now = new Date();

        List<PromoteAd> running = promoteAdRepository.findByStatus(PromoteStatus.Running);

        for (PromoteAd promote : running) {

            if (promote.getEndDate() != null && promote.getEndDate().before(now)) {

                promote.setStatus(PromoteStatus.Completed);

                // turn off promoted flag in posts table
                Posts post = promote.getPost();
                post.setPromoteFlag(false);
                postsRepository.save(post);

                promoteAdRepository.save(promote);
            }
        }
    }
}
