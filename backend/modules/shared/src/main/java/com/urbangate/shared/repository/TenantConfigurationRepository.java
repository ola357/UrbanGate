// Copyright (c) UrbanGate
package com.urbangate.shared.repository;

import com.urbangate.shared.entity.TenantConfiguration;
import java.util.Optional;

public interface TenantConfigurationRepository extends BaseRepository<TenantConfiguration, Long> {
  TenantConfiguration update(Long id, TenantConfiguration newConfiguration);

  Optional<TenantConfiguration> findByRealm(String realm);
}
