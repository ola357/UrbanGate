package com.estateresource.estatemanger.shared.repository;

import com.estateresource.estatemanger.shared.entity.EstateConfiguration;
import com.estateresource.estatemanger.shared.repository.impl.EstateConfigurationRepositoryImpl;

public interface EstateConfigurationRepository extends BaseRepository<EstateConfiguration, Long> {
    public EstateConfiguration update(Long id, EstateConfiguration newConfiguration);
}
