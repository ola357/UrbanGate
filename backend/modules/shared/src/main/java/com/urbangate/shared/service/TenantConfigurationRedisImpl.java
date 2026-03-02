// Copyright (c) UrbanGate
package com.urbangate.shared.service;

import com.urbangate.shared.entity.TenantConfiguration;
import com.urbangate.shared.repository.BaseRedisRepository;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TenantConfigurationRedisImpl
    implements BaseRedisRepository<TenantConfiguration, String> {

  private static final String NAMESPACE = "tenant::configuration::";

  private final RedisTemplate<String, Object> redisTemplate;
  private final Duration defaultTtl;

  public TenantConfigurationRedisImpl(
      RedisTemplate<String, Object> redisTemplate,
      @Value("${app.redis.default-ttl:3600}") long defaultTtlSeconds) {
    this.redisTemplate = redisTemplate;
    this.defaultTtl = Duration.ofMinutes(defaultTtlSeconds);
  }

  private String key(String id) {
    return NAMESPACE + ":" + id;
  }

  @Override
  public Optional<TenantConfiguration> findById(String id) {
    String fullKey = key(id);
    try {
      Object value = redisTemplate.opsForValue().get(fullKey);
      if (value == null) {
        log.debug("Cache MISS key={}", fullKey);
        return Optional.empty();
      }
      log.debug("Cache HIT  key={}", fullKey);
      return Optional.of((TenantConfiguration) value);
    } catch (Exception ex) {
      log.warn("Cache read failed key={}: {}", fullKey, ex.getMessage());
      return Optional.empty();
    }
  }

  public void save(TenantConfiguration tenantConfiguration) {
    save(tenantConfiguration, defaultTtl);
  }

  @Override
  public void save(TenantConfiguration tenantConfiguration, Duration ttl) {
    String fullKey = key(tenantConfiguration.getRealm());
    try {
      redisTemplate.opsForValue().set(fullKey, tenantConfiguration, ttl);
      log.info("Cache SET  key={} ttl={}", fullKey, ttl);
    } catch (Exception ex) {
      log.error("Cache save failed key={}: {}", fullKey, ex.getMessage(), ex);
    }
  }

  @Override
  public void evict(String id) {
    String fullKey = key(id);
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
  public boolean exists(String id) {
    try {
      return Boolean.TRUE.equals(redisTemplate.hasKey(key(id)));
    } catch (Exception ex) {
      log.warn("Cache exists check failed key={}: {}", key(id), ex.getMessage());
      return false;
    }
  }
}
