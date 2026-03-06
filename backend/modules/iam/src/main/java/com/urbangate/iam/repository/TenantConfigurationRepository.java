// Copyright (c) UrbanGate
package com.urbangate.iam.repository;

import com.urbangate.iam.entity.TenantConfiguration;
import com.urbangate.shared.repository.BaseRepository;
import java.util.Optional;

public interface TenantConfigurationRepository extends BaseRepository<TenantConfiguration, Long> {
  TenantConfiguration update(Long id, TenantConfiguration newConfiguration);

  Optional<TenantConfiguration> findByRealm(String realm);
}
