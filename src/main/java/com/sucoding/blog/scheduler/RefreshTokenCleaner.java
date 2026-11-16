package com.sucoding.blog.scheduler;

import com.sucoding.blog.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleaner {
    private final RefreshTokenService refreshTokenService;

    // 초 | 분 | 시 | 일 | 월 | 요일
    // 매일 새벽 4시. 사람이 가장 적게 쓰는 시간에 돈다
    @Scheduled(cron = "0 0 4 * * *")
    public void cleanExpiredTokens() {
        int deletedCount = refreshTokenService.deleteExpired();
        System.out.println("Deleted count: " + deletedCount);
    }
}
