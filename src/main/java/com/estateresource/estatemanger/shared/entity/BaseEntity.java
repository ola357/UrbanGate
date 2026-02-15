package com.estateresource.estatemanger.shared.entity;

import com.estateresource.estatemanger.shared.enums.EntityStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

@Getter @Setter
@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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
