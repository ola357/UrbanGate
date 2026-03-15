// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import com.urbangate.iam.domain.entity.UserProfileEntity;
import com.urbangate.iam.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

  private final UserProfileRepository repository;

  @Transactional
  public UserProfileEntity upsert(String subject, String email, String displayName) {
    return repository
        .findBySubject(subject)
        .map(
            existing -> {
              existing.setEmail(email);
              existing.setDisplayName(displayName);
              existing.touch();
              return repository.save(existing);
            })
        .orElseGet(() -> repository.save(UserProfileEntity.create(subject, email, displayName)));
  }
}
