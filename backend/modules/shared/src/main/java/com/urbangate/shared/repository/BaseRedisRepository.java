// Copyright (c) UrbanGate
package com.urbangate.shared.repository;

import java.time.Duration;
import java.util.Optional;

public interface BaseRedisRepository<T, ID> {

  public Optional<T> findById(ID key);

  public void save(T entity, Duration ttl);

  public void evict(ID key);

  public void evictAll();

  public boolean exists(ID key);
}
