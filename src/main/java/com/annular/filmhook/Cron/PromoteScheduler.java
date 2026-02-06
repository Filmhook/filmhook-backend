package com.annular.filmhook.Cron;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Scheduled(cron = "0 0 * * * *")  // every 1 hour
    public void autoCompletePromotions() {

        Date now = new Date();

        // 1) Load promote + post together in one query
        List<PromoteAd> runningPromotes =
                promoteAdRepository.findRunningPromotesWithPost(PromoteStatus.Running);

        if (runningPromotes.isEmpty()) return;

        // Lists for batch updates
        List<PromoteAd> toUpdatePromotes = new ArrayList<>();
        List<Posts> toUpdatePosts = new ArrayList<>();

        for (PromoteAd promote : runningPromotes) {

            if (promote.getEndDate() != null && promote.getEndDate().before(now)) {

                promote.setStatus(PromoteStatus.Completed);

                Posts post = promote.getPost();
                post.setPromoteFlag(false);

                toUpdatePromotes.add(promote);
                toUpdatePosts.add(post);
            }
        }

        // 2) Batch update only if needed
        if (!toUpdatePosts.isEmpty()) postsRepository.saveAll(toUpdatePosts);
        if (!toUpdatePromotes.isEmpty()) promoteAdRepository.saveAll(toUpdatePromotes);

        System.out.println("Scheduler updated " + toUpdatePromotes.size() + " promotions.");
    }
}
