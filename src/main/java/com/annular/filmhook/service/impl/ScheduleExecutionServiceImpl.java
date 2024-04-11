package com.annular.filmhook.service.impl;

import com.annular.filmhook.model.Story;
import com.annular.filmhook.service.ScheduleExecutionService;
import com.annular.filmhook.service.StoriesService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.List;

@Configuration
@EnableAsync
public class ScheduleExecutionServiceImpl implements ScheduleExecutionService {

    public static final Logger logger = LoggerFactory.getLogger(ScheduleExecutionServiceImpl.class);

    @Value("${jobs.enabled}")
    private boolean isEnabled;

    @Value("${jobs.cron.expression}")
    private String cronExpression;

    @Value("${jobs.cron.timezone}")
    private String timeZone;

    @Autowired
    StoriesService storiesService;

    @Override
    @Scheduled(cron = "${jobs.cron.expression}", zone = "${jobs.cron.timezone}")
    @Async
    public void expireStories() {
        if(isEnabled) {
            logger.info("Executing expireStories job at {}", new Date());
            List<Story> storyList = storiesService.getMoreThanOneDayStories();
            if (!storyList.isEmpty()) storiesService.deleteExpiredStories(storyList);
        }
    }
}
