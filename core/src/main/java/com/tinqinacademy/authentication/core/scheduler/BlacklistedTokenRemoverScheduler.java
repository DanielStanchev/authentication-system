package com.tinqinacademy.authentication.core.scheduler;

import com.tinqinacademy.authentication.persistence.entity.BlacklistedToken;
import com.tinqinacademy.authentication.persistence.repository.BlacklistedTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlacklistedTokenRemoverScheduler {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public BlacklistedTokenRemoverScheduler(BlacklistedTokenRepository blacklistedTokenRepository) {
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    @Scheduled(cron = "0 0 1 1/1 * ?")
    public void clearBlacklistedTokens(){
        List<BlacklistedToken> expiredTokens = blacklistedTokenRepository.getExpiredTokens();
        blacklistedTokenRepository.deleteAll(expiredTokens);
    }
}
