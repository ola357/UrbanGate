// Copyright (c) UrbanGate
package com.urbangate.shared.repository;

import java.time.Duration;
import java.util.Optional;

public interface BaseRedisRepository<T, K> {

  Optional<T> findById(K key);

  void save(T entity, Duration ttl);

  void evict(K key);

  void evictAll();

  boolean exists(K key);
}
