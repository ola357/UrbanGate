// Copyright (c) UrbanGate
package com.urbangate.shared.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/*
 *  @author vincent.enwere
 *  @Date 04/02/2026
 * */
@Repository
public interface BaseRepository<T, ID> {

  T insert(T entity);

  T update(T entity);

  Optional<T> findById(ID id);

  List<T> findAll();
}
