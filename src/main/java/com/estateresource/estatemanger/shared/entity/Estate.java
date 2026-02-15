package com.estateresource.estatemanger.shared.entity;

import lombok.*;

@Getter @Setter  @ToString
@NoArgsConstructor
@AllArgsConstructor
public class Estate extends BaseEntity {
    private Long id;
    private String name; //configurable
    private String description;
    private String icon;
    private Long creator;
    private String address;
    private String state;
    private String phone;
    private Long configurationId;
}
