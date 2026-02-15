package com.estateresource.estatemanger.security.repository;

import com.estateresource.estatemanger.security.model.entity.RefreshToken;
import com.estateresource.estatemanger.shared.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends BaseRepository<RefreshToken , Long> {

    Optional<RefreshToken> findByJti(String jti);

}
