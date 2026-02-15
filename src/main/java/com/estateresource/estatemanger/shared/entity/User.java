package com.estateresource.estatemanger.shared.entity;

import com.estateresource.estatemanger.security.model.entity.Role;
import com.estateresource.estatemanger.shared.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

@Entity
@Getter @Setter @ToString
public class User  {


    @Id
    private String id;

    private String phoneNumber;

    private Long estateId;

    private String password;

    private Set<Role> roles;

    private Timestamp createdOn;

    private Timestamp lastModifiedOn;

    @Enumerated(EnumType.STRING)
    private EntityStatus entityStatus;



    public void prepareForInsert(){
        Timestamp now  = Timestamp.from(Instant.now());

        if (this.createdOn == null){
            this.createdOn = now;
        }

        if (this.lastModifiedOn == null){
            this.lastModifiedOn = now;
        }

        if (this.entityStatus == null){
            this.entityStatus = EntityStatus.ACTIVE;
        }
    }

    public void prepareForUpdate(){
        this.lastModifiedOn = Timestamp.from(Instant.now());
    }


}
