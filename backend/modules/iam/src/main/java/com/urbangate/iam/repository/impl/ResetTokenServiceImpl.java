// Copyright (c) UrbanGate
package com.urbangate.iam.repository.impl;

import com.urbangate.shared.repository.BaseRedisRepository;
import java.time.Duration;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ResetTokenServiceImpl implements BaseRedisRepository<String, String> {

  private static final String NAMESPACE = "password::reset::";
  private final RedisTemplate<String, Object> redisTemplate;
  private final Duration defaultTtl;

  public ResetTokenServiceImpl(
      RedisTemplate<String, Object> redisTemplate,
      @Value("${app.redis.default-ttl-password-resets:15}") long defaultTtl) {
    this.redisTemplate = redisTemplate;
    this.defaultTtl = Duration.ofMinutes(defaultTtl);
  }

  @Override
  public Optional<String> findById(String key) {
    String fullKey = key(key);
    try {
      Object value = redisTemplate.opsForValue().get(fullKey);
      if (value == null) {
        log.debug("Cache MISS key={}", fullKey);
        return Optional.empty();
      }
      log.debug("Cache HIT  key={}", fullKey);
      return Optional.of((String) value);
    } catch (Exception ex) {
      log.warn("Cache read failed key={}: {}", fullKey, ex.getMessage());
      return Optional.empty();
    }
  }

  private String key(String id) {
    return NAMESPACE + ":" + id;
  }

  public boolean validate(String code, String email) {
    return findById(email).stream()
        .anyMatch(
            result -> {
              log.info("Validate email={}", email);
              return result.equals(code);
            });
  }

  public void save(String email, String code) {
    String fullKey = key(email);
    try {
      redisTemplate.opsForValue().set(fullKey, code, defaultTtl);
      log.info("Cache SET  key={} ttl={}", fullKey, defaultTtl);
    } catch (Exception ex) {
      log.error("Cache save failed key={}: {}", fullKey, ex.getMessage(), ex);
    }
  }

  @Override
  public void save(String entity, Duration ttl) {
    // Un-Used At the moment
  }

  @Override
  public void evict(String key) {
    String fullKey = key(key);
    try {
      redisTemplate.delete(fullKey);
      log.debug("Cache DEL  key={}", fullKey);
    } catch (Exception ex) {
      log.error("Cache evict failed key={}: {}", fullKey, ex.getMessage(), ex);
    }
  }

  @Override
  public void evictAll() {
    // Un-Used At The Moment
  }

  @Override
  public boolean exists(String key) {
    try {
      return Boolean.TRUE.equals(redisTemplate.hasKey(key(key)));
    } catch (Exception ex) {
      log.warn("Cache exists check failed key={}: {}", key(key), ex.getMessage());
      return false;
    }
  }
}
