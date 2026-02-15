package com.estateresource.estatemanger.resident.service.impl;

import com.estateresource.estatemanger.estate.repository.ActivationCodeRepository;
import com.estateresource.estatemanger.resident.dto.request.PasswordRequest;
import com.estateresource.estatemanger.resident.dto.response.PasswordResponse;
import com.estateresource.estatemanger.resident.service.PasswordResetService;
import com.estateresource.estatemanger.shared.entity.User;
import com.estateresource.estatemanger.shared.enums.ExceptionResponse;
import com.estateresource.estatemanger.shared.exceptions.DataBaseOperationException;
import com.estateresource.estatemanger.shared.exceptions.UserNameNotFoundException;
import com.estateresource.estatemanger.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivationCodeRepository activationCodeRepository;

    @Override
    public PasswordResponse resetPassword(PasswordRequest passwordRequest) {

        User user = userRepository.findById(passwordRequest.userId())
                .orElseThrow(()-> new UserNameNotFoundException(ExceptionResponse.USER_NAME_NOTFOUND));
        String hashedPassword = passwordEncoder.encode(passwordRequest.password());
        userRepository.updatePassword(user, hashedPassword);
        boolean revoked = activationCodeRepository.revokeCode(passwordRequest.activationToken());
        if (!revoked) {
            throw new DataBaseOperationException(ExceptionResponse.UNABLE_TO_REVOKE_ACTIVATION_CODE);
        }
        return new PasswordResponse(user.getId(), true);
    }
}
