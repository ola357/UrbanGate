package com.estateresource.estatemanger.shared.repository;


import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
*  @author vincent.enwere
*  @Date 04/02/2026
* */
@Repository
public interface BaseRepository<T , ID> {

    T insert(T entity);
    T update(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
}
