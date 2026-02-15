package com.estateresource.estatemanger.shared.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class ResidentProfile extends  BaseEntity {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String dateOfBirth;
    private String unitAddress;
    private String gender;

}
