package com.learn.task;

import com.learn.service.system.SystemService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SystemTask {
    private static final Logger log = LoggerFactory.getLogger(SystemTask.class);
    @Resource
    private SystemService systemService;

    @Scheduled(cron="0 38 0 * * ?", zone="Asia/Shanghai")
    public void archiveTodayVisitorJob() {
        log.info("开始归档今日访问量");
        this.systemService.archiveTodayVisitor();
        log.info("结束归档今日访问量");
    }
}
