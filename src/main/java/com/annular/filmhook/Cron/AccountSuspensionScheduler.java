package com.annular.filmhook.Cron;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.ReportPost;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.PostsRepository;
import com.annular.filmhook.repository.ReportRepository;
import com.annular.filmhook.repository.UserRepository;

@Component
public class AccountSuspensionScheduler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    PostsRepository postsRepository;

    // Cron job runs every day at midnight (adjust as needed)
    @Scheduled(cron = "0 0 0 * * ?")
    public void deactivateTemporarilySuspendedUsers() {
        Date sevenDaysAgo = Date.from(LocalDate.now().minusDays(7)
                                       .atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Fetch reports older than 7 days with deletePostSuspension = 1
        List<ReportPost> oldSuspensions = reportRepository.findOldSuspendedReports(sevenDaysAgo);

        for (ReportPost report : oldSuspensions) {
            Integer postId = report.getPostId();
            if (postId != null) {
                Optional<Posts> optionalPost = postsRepository.findById(postId);
                if (optionalPost.isPresent()) {
                    Posts post = optionalPost.get();
                    if (post.getUser() != null) {
                        User user = post.getUser();
                        user.setStatus(false);
                        userRepository.save(user);
                    }
                }
            }
        }

    }
}
