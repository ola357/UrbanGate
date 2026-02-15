package com.estateresource.estatemanger.security.model.entity;

import com.estateresource.estatemanger.shared.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;

import java.sql.Timestamp;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Entity @Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken  extends BaseEntity {

    private String jti;
    private String username;

    private Timestamp expiresAt;

    private boolean revoked;
}
