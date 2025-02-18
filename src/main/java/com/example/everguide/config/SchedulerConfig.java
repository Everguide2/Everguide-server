package com.example.everguide.config;

import com.example.everguide.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {

    private final MemberService memberService;

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void run() {

        memberService.updateRedis();
    }
}
