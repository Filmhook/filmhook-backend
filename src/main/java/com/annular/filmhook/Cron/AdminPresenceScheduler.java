package com.annular.filmhook.Cron;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.model.AdminOnlineSession;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.AdminOnlineSessionRepository;
import com.annular.filmhook.repository.UserRepository;
@Component
public class AdminPresenceScheduler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminOnlineSessionRepository sessionRepo;

    @Scheduled(fixedRate = 60000) // Runs every 1 minute
    @Transactional
    public void markOfflineAdmins() {

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(2);

        // Get admins who must be marked offline
        List<User> admins = userRepository.findAdminsToMarkOffline(threshold);

        for (User admin : admins) {

            // Fetch last open session
            AdminOnlineSession session = sessionRepo
                    .findOpenSession(admin.getUserId(), PageRequest.of(0, 1))
                    .stream()
                    .findFirst()
                    .orElse(null);

            // Close it
            if (session != null && session.getLogoutTime() == null) {
                session.setLogoutTime(LocalDateTime.now());
                sessionRepo.save(session);
            }
        }

        // Mark admins offline in DB
        userRepository.markAdminsOffline(threshold);
    }
}
