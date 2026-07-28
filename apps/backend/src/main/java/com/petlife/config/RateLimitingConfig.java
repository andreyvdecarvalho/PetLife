package com.petlife.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting para tentativas de login.
 * Limite: 5 tentativas por IP a cada 5 minutos.
 */
@Component
public class RateLimitingConfig {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        Bandwidth limit = Bandwidth.builder()
            .capacity(5)
            .refillIntervally(5, Duration.ofMinutes(5))
            .build();
        return Bucket.builder().addLimit(limit).build();
    }

    public boolean tryConsume(String ip) {
        return resolveBucket(ip).tryConsume(1);
    }
}
