// Copyright (c) UrbanGate
package com.urbangate.access.repository.impl;

import com.urbangate.access.entity.AccessCode;
import com.urbangate.shared.repository.BaseRedisRepository;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccessCodeRedisImpl implements BaseRedisRepository<AccessCode, String> {

  private static final String NAMESPACE = "access::code::";

  private final RedisTemplate<String, Object> redisTemplate;
  private final Duration defaultTtl;

  public AccessCodeRedisImpl(
      RedisTemplate<String, Object> redisTemplate,
      @Value("${app.redis.default-ttl:3600}") long defaultTtl) {
    this.redisTemplate = redisTemplate;
    this.defaultTtl = Duration.ofMinutes(defaultTtl);
  }

  private String key(String code) {
    return NAMESPACE + ":" + code;
  }

  @Override
  public Optional<AccessCode> findById(String code) {
    String fullKey = key(code);
    log.info("Get access code from key: {}", fullKey);
    try {
      Object value = redisTemplate.opsForValue().get(fullKey);
      if (value == null) {
        log.debug("Cache MISS key={}", fullKey);
        return Optional.empty();
      }
      log.debug("Cache HIT  key={}", fullKey);
      return Optional.of((AccessCode) value);
    } catch (Exception ex) {
      log.warn("Cache read failed key={}: {}", fullKey, ex.getMessage());
      return Optional.empty();
    }
  }

  public void save(AccessCode accessCode) {
    save(accessCode, defaultTtl);
  }

  @Override
  public void save(AccessCode accessCode, Duration ttl) {
    String fullKey = key(accessCode.getCode());
    try {
      redisTemplate.opsForValue().set(fullKey, accessCode, ttl);
      log.info("Cache SET  key={} ttl={}", fullKey, ttl);
    } catch (Exception ex) {
      log.error("Cache save failed key={}: {}", fullKey, ex.getMessage(), ex);
    }
  }

  @Override
  public void evict(String code) {
    String fullKey = key(code);
    try {
      redisTemplate.delete(fullKey);
      log.debug("Cache DEL  key={}", fullKey);
    } catch (Exception ex) {
      log.error("Cache evict failed key={}: {}", fullKey, ex.getMessage(), ex);
    }
  }

  @Override
  public void evictAll() {
    String pattern = NAMESPACE + ":*";
    try {
      Set<String> keys = redisTemplate.keys(pattern);
      if (keys != null && !keys.isEmpty()) {
        redisTemplate.delete(keys);
        log.info("Cache evictAll removed {} keys", keys.size());
      }
    } catch (Exception ex) {
      log.error("Cache evictAll failed: {}", ex.getMessage(), ex);
    }
  }

  @Override
  public boolean exists(String code) {
    try {
      return Boolean.TRUE.equals(redisTemplate.hasKey(key(code)));
    } catch (Exception ex) {
      log.warn("Cache exists check failed key={}: {}", key(code), ex.getMessage());
      return false;
    }
  }
}
