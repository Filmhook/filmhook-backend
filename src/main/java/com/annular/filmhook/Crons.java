package com.annular.filmhook;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.annular.filmhook.model.Promote;
import com.annular.filmhook.repository.PostsRepository;
import com.annular.filmhook.repository.PromoteRepository;

@Component
public class Crons {
	
	@Autowired
    private PromoteRepository promoteRepository;

    @Autowired
    private PostsRepository postRepository;
	
    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
    public void updatePromoteStatus() {
        Date currentDate = new Date();
        List<Promote> promotes = promoteRepository.findAll();

        for (Promote promote : promotes) {
            // Check if the number of days has passed
            long diffInMillis = currentDate.getTime() - promote.getCreatedOn().getTime();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

            if (diffInDays >= promote.getNumberOfDays()) {
                // Update post status
               // postRepository.updatePromoteStatus(promote.getPostId(), false);
                // Optionally, you can also update the promote flag here if necessary
            	 // Update post's promoteFlag and promoteStatus to false in the Post table
                postRepository.updatePromoteStatusAndFlag(promote.getPostId(), false, false);

                // Update the Promote row to set status as false
                promote.setStatus(false); // Assuming there's a 'status' field in Promote to track the active state
                promoteRepository.save(promote); 
            }
        }
    }

}
