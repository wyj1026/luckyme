package com.tegongdete.luckyme.plugin.limitter;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenBucketRateLimiter implements RequestRateLimiter {
    private final RateLimiter rateLimiter;

    public TokenBucketRateLimiter(@Value("${permits}") int permitsPerSecond) {
        rateLimiter = RateLimiter.create(permitsPerSecond);
    }

    public boolean isAccessible() {
        return rateLimiter.tryAcquire();
    }
}
