package com.estateresource.estatemanger.security.model.entity;

import com.estateresource.estatemanger.shared.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

    private com.estateresource.estatemanger.security.model.enums.Role name;
}
