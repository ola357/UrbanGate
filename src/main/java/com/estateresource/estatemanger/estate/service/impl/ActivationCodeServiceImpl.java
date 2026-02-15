package com.estateresource.estatemanger.estate.service.impl;

import com.estateresource.estatemanger.estate.dto.request.ActivationRequest;
import com.estateresource.estatemanger.estate.dto.response.ActivationResponse;
import com.estateresource.estatemanger.estate.entity.ActivationCode;
import com.estateresource.estatemanger.estate.repository.ActivationCodeRepository;
import com.estateresource.estatemanger.estate.service.ActivationCodeService;
import com.estateresource.estatemanger.shared.entity.ResidentProfile;
import com.estateresource.estatemanger.shared.entity.User;
import com.estateresource.estatemanger.shared.enums.ExceptionResponse;
import com.estateresource.estatemanger.shared.exceptions.DataBaseOperationException;
import com.estateresource.estatemanger.shared.repository.ResidentProfileRepository;
import com.estateresource.estatemanger.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivationCodeServiceImpl implements ActivationCodeService {

    private final ActivationCodeRepository activationCodeRepository;
    private final UserRepository userRepository;
    private final ResidentProfileRepository residentProfileRepository;

    @Override
    public ActivationResponse retrieveUserByActivationCode(ActivationRequest activationRequest) {

        ActivationCode code = activationCodeRepository.findByCode(activationRequest.token())
                .orElseThrow(() -> new DataBaseOperationException(ExceptionResponse.INVALID_ACTIVATION_CODE));

        if (code.isRevoked()){
            throw new DataBaseOperationException(ExceptionResponse.ACTIVATION_CODE_REVOKED);
        }

        User user = userRepository.findById(code.getUserId())
                .orElseThrow(()-> new DataBaseOperationException(ExceptionResponse.UNABLE_TO_FETCH_RECORD));

        ResidentProfile profile = residentProfileRepository.findByUserId(user.getId())
                .orElseThrow(()-> new DataBaseOperationException(ExceptionResponse.UNABLE_TO_FETCH_RECORD));

        return new ActivationResponse(
                profile.getFirstName(),
                profile.getLastName(),
                profile.getUnitAddress(),
                user.getPhoneNumber()
        );
    }


    @Scheduled(fixedDelayString = "${activation-code.revoke-schedule-time}")
    public void scheduleActivationCodeRevoke(){
        List<ActivationCode> activationCodes = activationCodeRepository.findAll();
        if (activationCodes.isEmpty()) {
            log.warn("No activation codes found");
        }
        activationCodes.forEach(activationCode -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime timestamp = activationCode.getCreatedOn().toLocalDateTime()
                    .plusHours(activationCode.getTtlInHours());
            Boolean isPastTtl = now.isAfter(timestamp);
            log.info("activation code found -{}", activationCode);
            if (!activationCode.isRevoked() && isPastTtl){
                activationCodeRepository.revokeCode(activationCode.getCode());
                log.info("Revoked activation code {}", activationCode.getCode());
            }

        });
    }
}
