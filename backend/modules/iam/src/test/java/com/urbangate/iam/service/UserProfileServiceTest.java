// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.urbangate.iam.domain.entity.UserProfileEntity;
import com.urbangate.iam.repository.UserProfileRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

  @Mock private UserProfileRepository repository;

  private UserProfileService service;

  @BeforeEach
  void setUp() {
    service = new UserProfileService(repository);
  }

  @Test
  void upsertUpdatesExistingProfile() {
    UserProfileEntity existing = UserProfileEntity.create("sub", "old@mail.com", "Old");
    when(repository.findBySubject("sub")).thenReturn(Optional.of(existing));
    when(repository.save(any(UserProfileEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    UserProfileEntity updated = service.upsert("sub", "new@mail.com", "New Name");

    assertEquals("new@mail.com", updated.getEmail());
    assertEquals("New Name", updated.getDisplayName());
    assertNotNull(updated.getUpdatedAt());
    verify(repository).save(existing);
  }

  @Test
  void upsertCreatesNewProfile() {
    when(repository.findBySubject("sub")).thenReturn(Optional.empty());
    when(repository.save(any(UserProfileEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    UserProfileEntity created = service.upsert("sub", "user@mail.com", "User");

    assertEquals("sub", created.getSubject());
    assertEquals("user@mail.com", created.getEmail());
    assertEquals("User", created.getDisplayName());
    assertNotNull(created.getCreatedAt());
  }
}
