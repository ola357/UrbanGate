package com.estateresource.estatemanger.estate.service.impl;

import com.estateresource.estatemanger.estate.dto.request.ResidentOnboardingRequest;
import com.estateresource.estatemanger.estate.dto.response.ResidentOnboardingResponse;
import com.estateresource.estatemanger.estate.entity.ActivationCode;
import com.estateresource.estatemanger.estate.repository.ActivationCodeRepository;
import com.estateresource.estatemanger.estate.service.ResidentOnboardingService;
import com.estateresource.estatemanger.security.model.entity.Role;
import com.estateresource.estatemanger.security.repository.RoleRepository;
import com.estateresource.estatemanger.shared.entity.Estate;
import com.estateresource.estatemanger.shared.entity.EstateConfiguration;
import com.estateresource.estatemanger.shared.entity.ResidentProfile;
import com.estateresource.estatemanger.shared.entity.User;
import com.estateresource.estatemanger.shared.enums.ExceptionResponse;
import com.estateresource.estatemanger.shared.exceptions.DataBaseOperationException;
import com.estateresource.estatemanger.shared.repository.EstateConfigurationRepository;
import com.estateresource.estatemanger.shared.repository.ResidentProfileRepository;
import com.estateresource.estatemanger.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.estateresource.estatemanger.security.model.enums.Role.ROLE_RESIDENT;

@Service
@RequiredArgsConstructor
public class ResidentOnboardingServiceImpl implements ResidentOnboardingService {

    private final ActivationCodeRepository activationCodeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EstateConfigurationRepository estateConfigurationRepository;
    private final ResidentProfileRepository residentProfileRepository;

    @Value("${activation-code.ttl-in-minutes:5}")
    private int activationCodeTtlInMinutes;


    @Override
    public ResidentOnboardingResponse onboardResident(ResidentOnboardingRequest residentOnboardingRequest, Estate estate) {

        User user = new User();

        user.setEstateId(estate.getId());
        user.setPhoneNumber(residentOnboardingRequest.phoneNumber());


        //TODO: Get Estate Configuration From redis/db
        EstateConfiguration estateConfiguration = estateConfigurationRepository.findById(estate.getConfigurationId())
                .orElseThrow(() -> new DataBaseOperationException(ExceptionResponse.UNABLE_TO_FETCH_RECORD));

        //TODO: Build UserID
        String userId = generateUniqueUserId(estateConfiguration);
        user.setId(userId);

        //Build Resident Profile
        saveResidentProfile(residentOnboardingRequest, userId);
        userRepository.insert(user);
        handleroleAssignment(residentOnboardingRequest, user);
        String token = saveActivationCode(userId);

        return new ResidentOnboardingResponse(estateConfiguration.getEstateId(), estate.getName(), userId, token);
    }

    private void handleroleAssignment(ResidentOnboardingRequest residentOnboardingRequest, User user) {
        Role role = roleRepository.findRoleByName(ROLE_RESIDENT.name())
                .orElseThrow();
        Set<Role> roles = Collections.singleton(role);
        user.setRoles(roles);
        roleRepository.assignRoleToUser(residentOnboardingRequest.phoneNumber(), role.getId());
    }

    private String saveActivationCode(String userId) {
        ActivationCode activationCode = new ActivationCode();
        activationCode.setTtlInHours(activationCodeTtlInMinutes);
        activationCode.setUserId(userId);

        String token = generateActivationCode(userId);
        activationCode.setCode(token);
        activationCodeRepository.insert(activationCode);
        return token;
    }

    private String generateActivationCode(String userId) {
        String randomPrefix = UUID.randomUUID()
                .toString()
                .replaceAll("[^A-Za-z]", "")
                .substring(0, 2)
                .toUpperCase();
        return userId + "-" + randomPrefix;
    }


    private void saveResidentProfile(ResidentOnboardingRequest residentOnboardingRequest, String userId) {
        ResidentProfile residentProfile = new ResidentProfile();
        residentProfile.setUserId(userId);
        residentProfile.setEmail(residentOnboardingRequest.email());
        residentProfile.setLastName(residentOnboardingRequest.lastName());
        residentProfile.setFirstName(residentOnboardingRequest.firstName());
        residentProfile.setGender(residentOnboardingRequest.gender());
        residentProfile.setUnitAddress(residentOnboardingRequest.unitAddress());
        residentProfileRepository.insert(residentProfile);
    }

    private  String generateUniqueUserId(EstateConfiguration estateConfiguration) {
        String estateCode = estateConfiguration.getEstateCode();
        String partOne = estateCode.substring(3);
        int code = ThreadLocalRandom.current().nextInt(1000, 10000);
        String userId = partOne + "-" + code;
        while (userRepository.findById(userId).isPresent()){
            userId = partOne + "-" + ThreadLocalRandom.current().nextInt(1000, 10000);
        }

        return userId;
    }
}
